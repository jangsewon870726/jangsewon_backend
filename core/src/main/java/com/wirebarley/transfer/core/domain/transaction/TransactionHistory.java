package com.wirebarley.transfer.core.domain.transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransactionHistory {

    public enum TransactionType {
        WITHDRAW,
        DEPOSIT,
        TRANSFER_SEND,
        TRANSFER_RECEIVE
    }

    private Long id;
    private Long accountId;
    private Long counterpartyAccountId;
    private TransactionType type;
    private BigDecimal amount;
    private BigDecimal fee;

    private LocalDateTime transactedAt;

    @Builder
    public TransactionHistory(Long id, Long accountId, Long counterpartyAccountId,
                              TransactionType type, BigDecimal amount, BigDecimal fee,
                              LocalDateTime transactedAt) {

        if (accountId == null || type == null || amount == null || fee == null || transactedAt == null) {
            throw new IllegalArgumentException("거래 내역 필수 필드가 누락되었습니다.");
        }

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("거래 금액(amount)은 0 이상의 값이어야 합니다.");
        }
        if (fee.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("수수료(fee)는 0 이상의 값이어야 합니다.");
        }

        this.id = id;
        this.accountId = accountId;
        this.counterpartyAccountId = counterpartyAccountId;
        this.type = type;

        this.amount = amount.setScale(0, RoundingMode.DOWN);
        this.fee = fee.setScale(0, RoundingMode.DOWN);
        this.transactedAt = transactedAt;
    }
}