package com.wirebarley.transfer.infra.entity.account;

import com.wirebarley.transfer.core.domain.account.Account;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "accounts")
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String accountNumber;

    @Column(nullable = false)
    private String ownerName;

    @Column(nullable = false)
    private BigDecimal balance;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Account.AccountStatus status;

    @Column(nullable = false, length = 256)
    private String passwordHash;

    @Column(nullable = false, length = 64)
    private String salt;

    @Builder
    public AccountEntity(Long id, String accountNumber, String ownerName, BigDecimal balance,
                         LocalDateTime createdAt, LocalDateTime updatedAt, Account.AccountStatus status,
                         String passwordHash, String salt) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = balance;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
        this.passwordHash = passwordHash;
        this.salt = salt;
    }
}