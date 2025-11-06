package com.wirebarley.transfer.core.repository;

import com.wirebarley.transfer.core.domain.account.Account;
import java.util.Optional;

public interface AccountRepository {

    Account save(Account account);

    Optional<Account> findById(Long id);

    Optional<Account> findByAccountNumber(String accountNumber);

    void delete(Account account);

    /**
     * Pessimistic Lock 을 사용하여 계좌 조회
     * (동시성 이슈 - 출금/이체 시 사용)
     * @param id 계좌 ID
     * @return 잠금 처리된 Account
     */
    Optional<Account> findByIdWithLock(Long id);
}