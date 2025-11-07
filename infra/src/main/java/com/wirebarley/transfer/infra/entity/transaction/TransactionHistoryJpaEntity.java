package com.wirebarley.transfer.infra.entity.transaction;

import com.wirebarley.transfer.core.domain.transaction.TransactionHistory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "transaction_history")
public class TransactionHistoryJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private Long accountId;

    @Column(updatable = false)
    private Long counterpartyAccountId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private TransactionHistory.TransactionType type;

    @Column(nullable = false, updatable = false)
    private BigDecimal amount;

    @Column(nullable = false, updatable = false)
    private BigDecimal fee;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime transactedAt;

    @Builder
    public TransactionHistoryJpaEntity(Long id, Long accountId, Long counterpartyAccountId,
                                       TransactionHistory.TransactionType type,
                                       BigDecimal amount, BigDecimal fee, LocalDateTime transactedAt) {
        this.id = id;
        this.accountId = accountId;
        this.counterpartyAccountId = counterpartyAccountId;
        this.type = type;
        this.amount = amount;
        this.fee = fee;
        this.transactedAt = transactedAt;
    }
}