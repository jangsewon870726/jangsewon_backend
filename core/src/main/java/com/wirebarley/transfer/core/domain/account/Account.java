package com.wirebarley.transfer.core.domain.account;

import com.wirebarley.transfer.core.common.security.SecurityUtil;
import com.wirebarley.transfer.core.exception.BusinessException;
import com.wirebarley.transfer.core.exception.ErrorCode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString(exclude = {"passwordHash", "salt"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
public class Account {

    public enum AccountStatus {
        ACTIVE,
        DELETED
    }

    private Long id;
    private String accountNumber;
    private String ownerName;
    private BigDecimal balance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private AccountStatus status;

    private String passwordHash;
    private String salt;

    private static void validateAmount(BigDecimal amount, ErrorCode errorCode) {
        if (amount == null) {
            throw new BusinessException(errorCode);
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(errorCode);
        }
        if (amount.scale() > 0) {
            throw new BusinessException("금액은 소수점을 포함할 수 없습니다.", errorCode);
        }
    }

    @Builder
    public Account(Long id, String accountNumber, String ownerName, BigDecimal balance,
                   LocalDateTime createdAt, LocalDateTime updatedAt, AccountStatus status,
                   String passwordHash, String salt) {

        if (balance == null || balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ErrorCode.INVALID_INITIAL_BALANCE);
        }
        if (accountNumber == null || ownerName == null || passwordHash == null || salt == null) {
            throw new IllegalArgumentException("계좌 생성 필수 필드가 누락되었습니다.");
        }

        this.id = id;
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = balance.setScale(0, RoundingMode.DOWN);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = (status == null) ? AccountStatus.ACTIVE : status;
        this.passwordHash = passwordHash;
        this.salt = salt;
    }

    public boolean verifyPassword(String password) {
        String hashed = SecurityUtil.hashPassword(password, this.salt);
        return this.passwordHash.equals(hashed);
    }

    public void withdraw(BigDecimal amount) {
        validateAmount(amount, ErrorCode.INVALID_WITHDRAW_AMOUNT);

        if (this.balance.compareTo(amount) < 0) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_FUNDS);
        }
        this.balance = this.balance.subtract(amount);
    }

    public void deposit(BigDecimal amount) {
        validateAmount(amount, ErrorCode.INVALID_DEPOSIT_AMOUNT);

        this.balance = this.balance.add(amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}