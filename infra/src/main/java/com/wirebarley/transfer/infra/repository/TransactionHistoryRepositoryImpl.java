package com.wirebarley.transfer.infra.repository;

import com.wirebarley.transfer.core.domain.transaction.TransactionHistory;
import com.wirebarley.transfer.core.repository.TransactionHistoryRepository;
import com.wirebarley.transfer.infra.entity.transaction.TransactionHistoryJpaEntity;
import com.wirebarley.transfer.infra.entity.transaction.TransactionHistoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TransactionHistoryRepositoryImpl implements TransactionHistoryRepository {

    private final SpringDataTransactionHistoryRepository jpaRepository;
    private final TransactionHistoryMapper mapper;

    @Override
    public TransactionHistory save(TransactionHistory history) {
        TransactionHistoryJpaEntity entity = mapper.toEntity(history);
        TransactionHistoryJpaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public List<TransactionHistory> findByAccountIdAndTypeAndTransactedAtAfter(
            Long accountId,
            TransactionHistory.TransactionType transactionType,
            LocalDateTime afterDateTime
    ) {
        List<TransactionHistoryJpaEntity> entities =
                jpaRepository.findByAccountIdAndTypeAndTransactedAtAfter(
                        accountId,
                        transactionType,
                        afterDateTime
                );

        return entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionHistory> findByAccountIdOrderByTransactedAtDesc(Long accountId) {
        List<TransactionHistoryJpaEntity> entities =
                jpaRepository.findByAccountIdOrderByTransactedAtDesc(accountId);

        return entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}