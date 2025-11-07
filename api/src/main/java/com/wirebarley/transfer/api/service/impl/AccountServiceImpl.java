package com.wirebarley.transfer.api.service.impl;

import com.wirebarley.transfer.core.repository.AccountRepository;
import com.wirebarley.transfer.core.dto.AccountDto;
import com.wirebarley.transfer.core.service.AccountService;
import com.wirebarley.transfer.core.common.security.SecurityUtil;
import com.wirebarley.transfer.core.domain.account.Account;
import com.wirebarley.transfer.core.domain.transaction.TransactionHistory;
import com.wirebarley.transfer.core.repository.TransactionHistoryRepository;
import com.wirebarley.transfer.core.exception.BusinessException;
import com.wirebarley.transfer.core.exception.ErrorCode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;

    private final SecureRandom random = new SecureRandom();

    @Value("${transfer.policy.fee-rate}")
    private BigDecimal FEE_RATE;

    @Value("${transfer.policy.daily-limit}")
    private BigDecimal TRANSFER_DAILY_LIMIT; // 1일 이체 한도 3000000

    @Value("${withdraw.policy.daily-limit}")
    private BigDecimal WITHDRAW_DAILY_LIMIT; // 1일 출금 한도 1000000

    @Value("${account.policy.prefix}")
    private String ACCOUNT_PREFIX; // 계좌번호 접두사 "110"


    @Override
    @Transactional
    public AccountDto.CreateResponse createAccount(AccountDto.CreateRequest request) {

        String salt = SecurityUtil.generateSalt();
        String passwordHash = SecurityUtil.hashPassword(request.getPassword(), salt);

        String newAccountNumber;
        do {
            newAccountNumber = generateNewAccountNumber();
        } while (accountRepository.existsByAccountNumber(newAccountNumber));

        LocalDateTime now = LocalDateTime.now();

        Account newAccount = Account.builder()
                .accountNumber(newAccountNumber)
                .ownerName(request.getOwnerName())
                .passwordHash(passwordHash)
                .salt(salt)
                .balance(request.getInitialBalance())
                .createdAt(now)
                .updatedAt(now)
                .status(Account.AccountStatus.ACTIVE)
                .build();

        Account savedAccount = accountRepository.save(newAccount);

        if (savedAccount.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            TransactionHistory initialDeposit = TransactionHistory.builder()
                    .accountId(savedAccount.getId())
                    .counterpartyAccountId(null)
                    .type(TransactionHistory.TransactionType.DEPOSIT)
                    .amount(savedAccount.getBalance())
                    .fee(BigDecimal.ZERO)
                    .transactedAt(savedAccount.getCreatedAt())
                    .build();

            transactionHistoryRepository.save(initialDeposit);
        }

        log.info("새 계좌 생성: {}", savedAccount.getAccountNumber());
        return AccountDto.CreateResponse.from(savedAccount);
    }

    @Override
    @Transactional
    public void deleteAccount(String accountNumber, AccountDto.DeleteRequest request) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));

        if (!account.verifyPassword(request.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_ACCOUNT_PASSWORD);
        }

        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new BusinessException(ErrorCode.BALANCE_NOT_ZERO);
        }

        Account deletedAccount = Account.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .ownerName(account.getOwnerName())
                .balance(account.getBalance())
                .createdAt(account.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .status(Account.AccountStatus.DELETED)
                .passwordHash(account.getPasswordHash())
                .salt(account.getSalt())
                .build();

        accountRepository.save(deletedAccount);
        log.info("계좌 삭제 (Soft Delete): {}", accountNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDto.BalanceResponse getAccountBalance(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));

        return AccountDto.BalanceResponse.from(account);
    }

    @Override
    @Transactional
    public AccountDto.TransferResponse transfer(AccountDto.TransferRequest request) {

        BigDecimal amount = request.getAmount();
        String fromAccountNumber = request.getFromAccountNumber();
        String toAccountNumber = request.getToAccountNumber();
        LocalDateTime now = LocalDateTime.now();

        if (fromAccountNumber.equals(toAccountNumber)) {
            throw new BusinessException(ErrorCode.SELF_TRANSFER_NOT_ALLOWED);
        }

        Account preCheckFromAccount = accountRepository.findByAccountNumber(fromAccountNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));
        Account preCheckToAccount = accountRepository.findByAccountNumber(toAccountNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));

        if (!preCheckFromAccount.verifyPassword(request.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_ACCOUNT_PASSWORD);
        }

        BigDecimal fee = amount.multiply(FEE_RATE).setScale(0, RoundingMode.DOWN);
        BigDecimal totalWithdrawAmount = amount.add(fee);

        LocalDateTime twentyFourHoursAgo = now.minusHours(24);
        List<TransactionHistory> recentTransactions =
                transactionHistoryRepository.findByAccountIdAndTypeAndTransactedAtAfter(
                        preCheckFromAccount.getId(),
                        TransactionHistory.TransactionType.TRANSFER_SEND,
                        twentyFourHoursAgo
                );
        BigDecimal totalTransferredToday = recentTransactions.stream()
                .map(TransactionHistory::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalTransferredToday.add(amount).compareTo(TRANSFER_DAILY_LIMIT) > 0) {
            throw new BusinessException(ErrorCode.DAILY_LIMIT_EXCEEDED);
        }

        Long id1 = preCheckFromAccount.getId();
        Long id2 = preCheckToAccount.getId();
        Account lockedFirst = (id1 < id2) ?
                lockAccountById(id1) : lockAccountById(id2);
        Account lockedSecond = (id1 < id2) ?
                lockAccountById(id2) : lockAccountById(id1);
        Account fromAccount = (lockedFirst.getId().equals(id1)) ? lockedFirst : lockedSecond;
        Account toAccount = (lockedSecond.getId().equals(id2)) ? lockedSecond : lockedFirst;

        fromAccount.withdraw(totalWithdrawAmount);
        toAccount.deposit(amount);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        TransactionHistory sendHistory = TransactionHistory.builder()
                .accountId(fromAccount.getId())
                .counterpartyAccountId(toAccount.getId())
                .type(TransactionHistory.TransactionType.TRANSFER_SEND)
                .amount(amount)
                .fee(fee)
                .transactedAt(now)
                .build();
        TransactionHistory receiveHistory = TransactionHistory.builder()
                .accountId(toAccount.getId())
                .counterpartyAccountId(fromAccount.getId())
                .type(TransactionHistory.TransactionType.TRANSFER_RECEIVE)
                .amount(amount)
                .fee(BigDecimal.ZERO)
                .transactedAt(now)
                .build();
        transactionHistoryRepository.save(sendHistory);
        transactionHistoryRepository.save(receiveHistory);

        log.info("송금 성공: {} -> {} (원금: {}, 수수료: {})",
                fromAccountNumber, toAccountNumber, amount, fee);

        return AccountDto.TransferResponse.of(fromAccount, toAccount, amount);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDto.HistoryResponse getTransactionHistory(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));

        List<TransactionHistory> histories =
                transactionHistoryRepository.findByAccountIdOrderByTransactedAtDesc(account.getId());

        List<AccountDto.TransactionDetail> transactionDetails = histories.stream()
                .map(history -> {
                    String counterpartyNumber = null;
                    String counterpartyName = null;

                    if (history.getCounterpartyAccountId() != null) {
                        Optional<Account> counterpartyOpt =
                                accountRepository.findById(history.getCounterpartyAccountId());

                        if (counterpartyOpt.isPresent()) {
                            Account counterparty = counterpartyOpt.get();
                            counterpartyNumber = counterparty.getAccountNumber();
                            counterpartyName = (counterparty.getStatus() == Account.AccountStatus.DELETED) ?
                                    "(삭제된 계좌)" : counterparty.getOwnerName();
                        } else {
                            counterpartyNumber = "N/A";
                            counterpartyName = "알 수 없음";
                        }
                    }

                    return AccountDto.TransactionDetail.builder()
                            .type(history.getType())
                            .amount(history.getAmount())
                            .fee(history.getFee())
                            .transactedAt(history.getTransactedAt())
                            .counterpartyAccountNumber(counterpartyNumber)
                            .counterpartyOwnerName(counterpartyName)
                            .build();
                })
                .collect(Collectors.toList());

        return new AccountDto.HistoryResponse(accountNumber, transactionDetails);
    }

    @Override
    @Transactional
    public AccountDto.DepositResponse deposit(AccountDto.DepositRequest request) {
        Account preCheckAccount = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));

        Account account = lockAccountById(preCheckAccount.getId());

        account.deposit(request.getAmount());

        accountRepository.save(account);

        TransactionHistory depositHistory = TransactionHistory.builder()
                .accountId(account.getId())
                .counterpartyAccountId(null)
                .type(TransactionHistory.TransactionType.DEPOSIT)
                .amount(request.getAmount())
                .fee(BigDecimal.ZERO)
                .transactedAt(LocalDateTime.now())
                .build();

        transactionHistoryRepository.save(depositHistory);

        log.info("입금 성공: {} ({}원)", account.getAccountNumber(), request.getAmount());

        return AccountDto.DepositResponse.from(account);
    }

    @Override
    @Transactional
    public AccountDto.WithdrawResponse withdraw(AccountDto.WithdrawRequest request) {
        BigDecimal amount = request.getAmount();
        LocalDateTime now = LocalDateTime.now();

        Account preCheckAccount = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));

        if (!preCheckAccount.verifyPassword(request.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_ACCOUNT_PASSWORD);
        }

        LocalDateTime twentyFourHoursAgo = now.minusHours(24);
        List<TransactionHistory> recentWithdrawals =
                transactionHistoryRepository.findByAccountIdAndTypeAndTransactedAtAfter(
                        preCheckAccount.getId(),
                        TransactionHistory.TransactionType.WITHDRAW,
                        twentyFourHoursAgo
                );
        BigDecimal totalWithdrawnToday = recentWithdrawals.stream()
                .map(TransactionHistory::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalWithdrawnToday.add(amount).compareTo(WITHDRAW_DAILY_LIMIT) > 0) {
            throw new BusinessException(ErrorCode.WITHDRAW_DAILY_LIMIT_EXCEEDED);
        }

        Account account = lockAccountById(preCheckAccount.getId());

        account.withdraw(amount);

        accountRepository.save(account);

        TransactionHistory withdrawHistory = TransactionHistory.builder()
                .accountId(account.getId())
                .counterpartyAccountId(null)
                .type(TransactionHistory.TransactionType.WITHDRAW)
                .amount(amount)
                .fee(BigDecimal.ZERO)
                .transactedAt(now)
                .build();

        transactionHistoryRepository.save(withdrawHistory);

        log.info("출금 성공: {} ({}원)", account.getAccountNumber(), amount);

        return AccountDto.WithdrawResponse.from(account);
    }

    /**
     * ID로 'ACTIVE' 계좌를 조회하며 PESSIMISTIC_WRITE 락을 획득하는 헬퍼 메서드
     */
    private Account lockAccountById(Long id) {
        return accountRepository.findByIdWithLock(id)
                .orElseThrow(() -> new BusinessException("활성 상태의 계좌를 찾을 수 없습니다.", ErrorCode.ACCOUNT_NOT_FOUND));
    }

    /**
     * 신규 계좌 번호를 생성하는 헬퍼 메서드
     * (e.g., "110-123-456789")
     */
    private String generateNewAccountNumber() {
        // "110" + "-" + 3자리 + "-" + 6자리
        String middlePart = String.format("%03d", random.nextInt(1000));
        String lastPart = String.format("%06d", random.nextInt(1000000));

        return ACCOUNT_PREFIX + "-" + middlePart + "-" + lastPart;
    }
}