package com.wirebarley.transfer.infra.repository;

import com.wirebarley.transfer.core.domain.transaction.TransactionHistory;
import com.wirebarley.transfer.infra.entity.transaction.TransactionHistoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SpringDataTransactionHistoryRepository
        extends JpaRepository<TransactionHistoryJpaEntity, Long> {

    List<TransactionHistoryJpaEntity> findByAccountIdAndTypeAndTransactedAtAfter(
            Long accountId,
            TransactionHistory.TransactionType transactionType,
            LocalDateTime transactedAt
    );

    List<TransactionHistoryJpaEntity> findByAccountIdOrderByTransactedAtDesc(Long accountId);
}