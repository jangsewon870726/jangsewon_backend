package com.wirebarley.transfer.core.dto;

import com.wirebarley.transfer.core.domain.account.Account;
import com.wirebarley.transfer.core.domain.transaction.TransactionHistory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class AccountDto {

    @Getter
    @Schema(description = "계좌 생성 요청 DTO")
    public static class CreateRequest {

        @Schema(description = "계좌 소유주명", example = "김철수")
        @NotBlank(message = "소유주명은 필수입니다.")
        private String ownerName;

        @Schema(description = "계좌 비밀번호 (숫자 4자리)", example = "1234")
        @NotBlank(message = "비밀번호는 필수입니다.")
        @Pattern(regexp = "^\\d{4}$", message = "비밀번호는 숫자 4자리여야 합니다.")
        private String password;

        @Schema(description = "초기 입금액", example = "50000")
        @Digits(integer = 19, fraction = 0, message = "금액은 소수점을 포함할 수 없습니다.")
        private BigDecimal initialBalance;

        public CreateRequest() {}

        public CreateRequest(String ownerName, String password, BigDecimal initialBalance) {
            this.ownerName = ownerName;
            this.password = password;
            this.initialBalance = initialBalance;
        }
    }

    @Getter
    @RequiredArgsConstructor
    @Schema(description = "계좌 생성 응답 DTO")
    public static class CreateResponse {

        @Schema(description = "생성된 계좌 ID", example = "1")
        private final Long accountId;

        @Schema(description = "생성된 계좌 번호", example = "110-123-456789")
        private final String accountNumber;

        @Schema(description = "계좌 소유주명", example = "김철수")
        private final String ownerName;

        @Schema(description = "현재 잔액", example = "50000")
        private final BigDecimal balance;


        public static CreateResponse from(Account account) {
            return new CreateResponse(
                    account.getId(),
                    account.getAccountNumber(),
                    account.getOwnerName(),
                    account.getBalance()
            );
        }
    }


    @Getter
    @Schema(description = "계좌 삭제 요청 DTO")
    public static class DeleteRequest {
        @Schema(description = "계좌 비밀번호 (숫자 4자리)", example = "1234")
        @NotBlank(message = "비밀번호는 필수입니다.")
        @Pattern(regexp = "^\\d{4}$", message = "비밀번호는 숫자 4자리여야 합니다.")
        private String password;

        public DeleteRequest() {}
        public DeleteRequest(String password) {
            this.password = password;
        }
    }


    @Getter
    @RequiredArgsConstructor
    @Schema(description = "계좌 잔액 조회 응답 DTO")
    public static class BalanceResponse {
        @Schema(description = "계좌 소유주명", example = "홍길동")
        private final String ownerName;
        @Schema(description = "계좌 잔액", example = "100000")
        private final BigDecimal balance;
        public static BalanceResponse from(Account account) {
            return new BalanceResponse(account.getOwnerName(), account.getBalance());
        }
    }


    @Getter
    @Schema(description = "송금 요청 DTO")
    public static class TransferRequest {
        @Schema(description = "보내는 사람 계좌 번호", example = "110-123-456789")
        @NotBlank(message = "보내는 사람 계좌번호는 필수입니다.")
        private String fromAccountNumber;
        @Schema(description = "보내는 사람 계좌 비밀번호 (숫자 4자리)", example = "1234")
        @NotBlank(message = "비밀번호는 필수입니다.")
        @Pattern(regexp = "^\\d{4}$", message = "비밀번호는 숫자 4자리여야 합니다.")
        private String password;
        @Schema(description = "받는 사람 계좌 번호", example = "120-456-789012")
        @NotBlank(message = "받는 사람 계좌번호는 필수입니다.")
        private String toAccountNumber;
        @Schema(description = "송금액", example = "15000")
        @Digits(integer = 19, fraction = 0, message = "금액은 소수점을 포함할 수 없습니다.")
        private BigDecimal amount;

        public TransferRequest() {}
        public TransferRequest(String from, String password, String to, BigDecimal amount) {
            this.fromAccountNumber = from;
            this.password = password;
            this.toAccountNumber = to;
            this.amount = amount;
        }
    }

    @Getter
    @RequiredArgsConstructor
    @Schema(description = "송금 결과 응답 DTO")
    public static class TransferResponse {
        @Schema(description = "보낸 사람 계좌 번호", example = "110-123-456789")
        private final String fromAccountNumber;
        @Schema(description = "보낸 계좌의 남은 잔액", example = "84850")
        private final BigDecimal fromAccountBalance;
        @Schema(description = "받은 사람 계좌 번호", example = "120-456-789012")
        private final String toAccountNumber;
        @Schema(description = "받은 계좌의 총 잔액", example = "65000")
        private final BigDecimal toAccountBalance;
        @Schema(description = "실제 송금된 금액", example = "15000")
        private final BigDecimal transferredAmount;
        public static TransferResponse of(Account fromAccount, Account toAccount, BigDecimal amount) {
            return new TransferResponse(
                    fromAccount.getAccountNumber(),
                    fromAccount.getBalance(),
                    toAccount.getAccountNumber(),
                    toAccount.getBalance(),
                    amount
            );
        }
    }


    @Getter
    @Schema(description = "출금 요청 DTO")
    public static class WithdrawRequest {
        @Schema(description = "출금할 계좌 번호", example = "110-123-456789")
        @NotBlank(message = "계좌번호는 필수입니다.")
        private String accountNumber;
        @Schema(description = "계좌 비밀번호 (숫자 4자리)", example = "1234")
        @NotBlank(message = "비밀번호는 필수입니다.")
        @Pattern(regexp = "^\\d{4}$", message = "비밀번호는 숫자 4자리여야 합니다.")
        private String password;
        @Schema(description = "출금액", example = "30000")
        @Digits(integer = 19, fraction = 0, message = "금액은 소수점을 포함할 수 없습니다.")
        private BigDecimal amount;

        public WithdrawRequest() {}
        public WithdrawRequest(String accountNumber, String password, BigDecimal amount) {
            this.accountNumber = accountNumber;
            this.password = password;
            this.amount = amount;
        }
    }

    @Getter
    @RequiredArgsConstructor
    @Schema(description = "출금 결과 응답 DTO")
    public static class WithdrawResponse {
        @Schema(description = "출금된 계좌 번호", example = "110-123-456789")
        private final String accountNumber;
        @Schema(description = "출금 후 남은 잔액", example = "70000")
        private final BigDecimal finalBalance;
        public static WithdrawResponse from(Account account) {
            return new WithdrawResponse(
                    account.getAccountNumber(),
                    account.getBalance()
            );
        }
    }


    @Getter
    @Builder
    @Schema(description = "단일 거래 내역 상세 DTO")
    public static class TransactionDetail {
        @Schema(description = "거래 유형 (TRANSFER_SEND/TRANSFER_RECEIVE)", example = "TRANSFER_SEND")
        private final TransactionHistory.TransactionType type;
        @Schema(description = "거래 금액 (원금)", example = "15000")
        private final BigDecimal amount;
        @Schema(description = "수수료", example = "150")
        private final BigDecimal fee;
        @Schema(description = "거래 시간")
        private final LocalDateTime transactedAt;
        @Schema(description = "상대방 계좌 번호", example = "120-456-789012")
        private final String counterpartyAccountNumber;
        @Schema(description = "상대방 계좌 소유주명", example = "이순신")
        private final String counterpartyOwnerName;
    }

    @Getter
    @RequiredArgsConstructor
    @Schema(description = "거래 내역 조회 응답 DTO")
    public static class HistoryResponse {
        @Schema(description = "조회된 계좌 번호", example = "110-123-456789")
        private final String accountNumber;
        @Schema(description = "거래 내역 목록 (최신순)")
        private final List<TransactionDetail> transactions;
    }


    @Getter
    @Schema(description = "입금 요청 DTO")
    public static class DepositRequest {
        @Schema(description = "입금할 계좌 번호", example = "110-123-456789")
        @NotBlank(message = "계좌번호는 필수입니다.")
        private String accountNumber;
        @Schema(description = "입금액", example = "100000")
        @Digits(integer = 19, fraction = 0, message = "금액은 소수점을 포함할 수 없습니다.")
        private BigDecimal amount;

        public DepositRequest() {}
        public DepositRequest(String accountNumber, BigDecimal amount) {
            this.accountNumber = accountNumber;
            this.amount = amount;
        }
    }

    @Getter
    @RequiredArgsConstructor
    @Schema(description = "입금 결과 응답 DTO")
    public static class DepositResponse {
        @Schema(description = "입금된 계좌 번호", example = "110-123-456789")
        private final String accountNumber;
        @Schema(description = "입금 후 최종 잔액", example = "200000")
        private final BigDecimal finalBalance;

        public static DepositResponse from(Account account) {
            return new DepositResponse(
                    account.getAccountNumber(),
                    account.getBalance()
            );
        }
    }
}