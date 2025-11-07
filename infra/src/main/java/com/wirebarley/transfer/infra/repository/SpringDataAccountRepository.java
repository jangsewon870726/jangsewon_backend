package com.wirebarley.transfer.infra.repository;

import com.wirebarley.transfer.core.domain.account.Account;
import com.wirebarley.transfer.infra.entity.account.AccountEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SpringDataAccountRepository extends JpaRepository<AccountEntity, Long> {

    Optional<AccountEntity> findByAccountNumberAndStatus(String accountNumber, Account.AccountStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM AccountEntity a WHERE a.id = :id AND a.status = :status")
    Optional<AccountEntity> findByIdWithLockAndStatus(@Param("id") Long id, @Param("status") Account.AccountStatus status);

    boolean existsByAccountNumberAndStatus(String accountNumber, Account.AccountStatus status);

    boolean existsByAccountNumber(String accountNumber);
}