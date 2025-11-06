package com.wirebarley.transfer.core.exception;

public enum ErrorCode {
    // 잔액 부족
    INSUFFICIENT_FUNDS,
    // 잘못된 입금 금액
    INVALID_DEPOSIT_AMOUNT,
    // 계좌 없음
    ACCOUNT_NOT_FOUND,
    // 초기 잔액 오류
    INVALID_INITIAL_BALANCE,
    // 잘못된 출금 금액
    INVALID_WITHDRAW_AMOUNT
}