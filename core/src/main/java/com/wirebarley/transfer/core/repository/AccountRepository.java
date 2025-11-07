package com.wirebarley.transfer.core.repository;

import com.wirebarley.transfer.core.domain.account.Account;
import java.util.Optional;

public interface AccountRepository {

    Account save(Account account);
    Optional<Account> findById(Long id);
    Optional<Account> findByAccountNumber(String accountNumber);
    void delete(Account account);
    Optional<Account> findByIdWithLock(Long id);
    boolean existsByAccountNumber(String accountNumber);
}