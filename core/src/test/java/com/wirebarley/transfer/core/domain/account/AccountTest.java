package com.wirebarley.transfer.core.domain.account;

import com.wirebarley.transfer.core.exception.BusinessException;
import com.wirebarley.transfer.core.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountTest {

    private String validSalt;
    private String validPasswordHash;
    private final String rawPassword = "1234";

    private Account.AccountBuilder createDefaultAccountBuilder() {
        return Account.builder()
                .id(1L)
                .accountNumber("110-123-456789")
                .ownerName("테스터")
                .balance(new BigDecimal("10000")) // 기본 잔액 10000원
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(Account.AccountStatus.ACTIVE)
                .passwordHash(validPasswordHash)
                .salt(validSalt);
    }

    @BeforeEach
    void setUp() {
        // 테스트 실행 전, 유효한 salt와 해시값을 미리 생성
        validSalt = "test-salt-123"; // 테스트에서는 고정된 salt 사용
        // SecurityUtil은 static 메서드이므로 직접 호출 가능
        validPasswordHash = com.wirebarley.transfer.core.common.security.SecurityUtil.hashPassword(rawPassword, validSalt);
    }

    @Nested
    @DisplayName("계좌 생성 (Builder)")
    class Describe_CreateAccount {

        @Test
        @DisplayName("초기 잔액이 0원 미만이면 예외가 발생한다")
        void it_throws_exception_when_balance_is_negative() {
            assertThatThrownBy(() -> createDefaultAccountBuilder()
                    .balance(new BigDecimal("-100")) // 잔액 -100원
                    .build())
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_INITIAL_BALANCE);
        }

        @Test
        @DisplayName("초기 잔액이 0원이면 정상 생성된다")
        void it_succeeds_when_balance_is_zero() {
            Account account = createDefaultAccountBuilder()
                    .balance(BigDecimal.ZERO)
                    .build();
            assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("초기 잔액에 소수점이 있어도 정수로 정규화(버림)된다")
        void it_normalizes_balance_when_scale_is_not_zero() {
            Account account = createDefaultAccountBuilder()
                    .balance(new BigDecimal("10000.99")) // 10000.99원
                    .build();
            // 10000원으로 저장되어야 함
            assertThat(account.getBalance()).isEqualByComparingTo(new BigDecimal("10000"));
        }
    }

    @Nested
    @DisplayName("출금 (withdraw)")
    class Describe_Withdraw {

        @Test
        @DisplayName("정상 금액을 출금하면 잔액이 감소한다")
        void it_succeeds_and_decreases_balance() {
            Account account = createDefaultAccountBuilder()
                    .balance(new BigDecimal("10000"))
                    .build();

            account.withdraw(new BigDecimal("3000"));

            assertThat(account.getBalance()).isEqualByComparingTo(new BigDecimal("7000"));
        }

        @Test
        @DisplayName("잔액보다 큰 금액을 출금하면 예외가 발생한다")
        void it_throws_exception_when_insufficient_funds() {
            Account account = createDefaultAccountBuilder()
                    .balance(new BigDecimal("10000"))
                    .build();

            assertThatThrownBy(() -> account.withdraw(new BigDecimal("10001")))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INSUFFICIENT_FUNDS);
        }

        @Test
        @DisplayName("0원 또는 음수 금액을 출금하면 예외가 발생한다")
        void it_throws_exception_when_invalid_amount() {
            Account account = createDefaultAccountBuilder().build();

            assertThatThrownBy(() -> account.withdraw(BigDecimal.ZERO)) // 0원
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_WITHDRAW_AMOUNT);

            assertThatThrownBy(() -> account.withdraw(new BigDecimal("-100"))) // 음수
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_WITHDRAW_AMOUNT);
        }

        @Test
        @DisplayName("소수점이 있는 금액을 출금하면 예외가 발생한다")
        void it_throws_exception_when_amount_has_scale() {
            Account account = createDefaultAccountBuilder().build();

            assertThatThrownBy(() -> account.withdraw(new BigDecimal("1000.50")))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_WITHDRAW_AMOUNT);
        }
    }

    @Nested
    @DisplayName("비밀번호 검증 (verifyPassword)")
    class Describe_VerifyPassword {

        @Test
        @DisplayName("올바른 비밀번호를 입력하면 true를 반환한다")
        void it_returns_true_when_password_is_correct() {
            Account account = createDefaultAccountBuilder().build();

            boolean result = account.verifyPassword("1234");

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("잘못된 비밀번호를 입력하면 false를 반환한다")
        void it_returns_false_when_password_is_incorrect() {
            Account account = createDefaultAccountBuilder().build();

            boolean result = account.verifyPassword("0000");

            assertThat(result).isFalse();
        }
    }
}