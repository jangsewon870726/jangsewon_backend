package com.wirebarley.transfer.infra.repository;

import com.wirebarley.transfer.core.domain.account.Account;
import com.wirebarley.transfer.core.repository.AccountRepository;
import com.wirebarley.transfer.infra.entity.account.AccountEntity;
import com.wirebarley.transfer.infra.entity.account.AccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AccountRepositoryImpl implements AccountRepository {

    private final SpringDataAccountRepository jpaRepository;

    @Override
    public Account save(Account account) {
        AccountEntity entity = AccountMapper.toEntity(account);
        AccountEntity savedEntity = jpaRepository.save(entity);
        return AccountMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Account> findById(Long id) {
        return jpaRepository.findById(id)
                .map(AccountMapper::toDomain);
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        return jpaRepository.findByAccountNumberAndStatus(accountNumber, Account.AccountStatus.ACTIVE)
                .map(AccountMapper::toDomain);
    }

    @Override
    public void delete(Account account) {
        AccountEntity entity = AccountMapper.toEntity(account);
        jpaRepository.delete(entity);
    }

    @Override
    public Optional<Account> findByIdWithLock(Long id) {
        return jpaRepository.findByIdWithLockAndStatus(id, Account.AccountStatus.ACTIVE)
                .map(AccountMapper::toDomain);
    }

    @Override
    public boolean existsByAccountNumber(String accountNumber) {
        return jpaRepository.existsByAccountNumber(accountNumber);
    }
}