package com.wirebarley.transfer.infra.entity.transaction;

import com.wirebarley.transfer.core.domain.transaction.TransactionHistory;
import org.springframework.stereotype.Component;

@Component
public class TransactionHistoryMapper {

    public TransactionHistoryJpaEntity toEntity(TransactionHistory domain) {
        if (domain == null) {
            return null;
        }

        return TransactionHistoryJpaEntity.builder()
                .id(domain.getId())
                .accountId(domain.getAccountId())
                .counterpartyAccountId(domain.getCounterpartyAccountId())
                .type(domain.getType())
                .amount(domain.getAmount())
                .fee(domain.getFee())
                .transactedAt(domain.getTransactedAt())
                .build();
    }

    public TransactionHistory toDomain(TransactionHistoryJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return TransactionHistory.builder()
                .id(entity.getId())
                .accountId(entity.getAccountId())
                .counterpartyAccountId(entity.getCounterpartyAccountId())
                .type(entity.getType())
                .amount(entity.getAmount())
                .fee(entity.getFee())
                .transactedAt(entity.getTransactedAt())
                .build();
    }
}