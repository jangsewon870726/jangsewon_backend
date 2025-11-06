package com.wirebarley.transfer.infra.entity.account;

import com.wirebarley.transfer.core.domain.account.Account;

public class AccountMapper {

    /**
     * Core Domain (Account) -> Infra Entity (AccountEntity)
     * DB에 저장/수정하기 위해 변환
     */
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
                .build();
    }

    /**
     * Infra Entity (AccountEntity) -> Core Domain (Account)
     * DB에서 조회한 데이터를 비즈니스 로직에 전달하기 위해 변환
     */
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
                .build();
    }
}