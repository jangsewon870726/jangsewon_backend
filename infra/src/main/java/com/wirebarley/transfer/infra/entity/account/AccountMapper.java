package com.wirebarley.transfer.infra.entity.account;

import com.wirebarley.transfer.core.domain.account.Account;

public class AccountMapper {

    public static AccountEntity toEntity(Account account) {
        if (account == null) {
            return null;
        }

        return AccountEntity.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .ownerName(account.getOwnerName())
                .balance(account.getBalance())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .status(account.getStatus())
                .passwordHash(account.getPasswordHash())
                .salt(account.getSalt())
                .build();
    }

    public static Account toDomain(AccountEntity entity) {
        if (entity == null) {
            return null;
        }

        return Account.builder()
                .id(entity.getId())
                .accountNumber(entity.getAccountNumber())
                .ownerName(entity.getOwnerName())
                .balance(entity.getBalance())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .status(entity.getStatus())
                .passwordHash(entity.getPasswordHash())
                .salt(entity.getSalt())
                .build();
    }
}