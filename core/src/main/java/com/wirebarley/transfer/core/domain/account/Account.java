package com.wirebarley.transfer.core.domain.account;

import com.wirebarley.transfer.core.exception.InsufficientFundsException;
import com.wirebarley.transfer.core.exception.InvalidDepositAmountException;
import com.wirebarley.transfer.core.exception.InvalidInitialBalanceException;
import com.wirebarley.transfer.core.exception.InvalidWithdrawAmountException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {
    private Long id;
    private String accountNumber;
    private String ownerName;
    private BigDecimal balance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public Account(Long id, String accountNumber, String ownerName, BigDecimal balance, LocalDateTime createdAt, LocalDateTime updatedAt) {
        if (balance == null || balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidInitialBalanceException();
        }
        this.id = id;
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = balance;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void withdraw(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidWithdrawAmountException();
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException();
        }
        this.balance = this.balance.subtract(amount);
    }

    public void deposit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidDepositAmountException();
        }
        this.balance = this.balance.add(amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Account account = (Account) o;
        return id != null && Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}