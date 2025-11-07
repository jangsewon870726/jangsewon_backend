package com.wirebarley.transfer.core.repository;

import com.wirebarley.transfer.core.domain.transaction.TransactionHistory;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionHistoryRepository {

    TransactionHistory save(TransactionHistory history);

    List<TransactionHistory> findByAccountIdAndTypeAndTransactedAtAfter(
            Long accountId,
            TransactionHistory.TransactionType transactionType,
            LocalDateTime afterDateTime
    );

    List<TransactionHistory> findByAccountIdOrderByTransactedAtDesc(Long accountId);
}