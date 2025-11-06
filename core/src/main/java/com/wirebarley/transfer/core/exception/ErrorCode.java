package com.wirebarley.transfer.core.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    //private final HttpStatus status;

    INSUFFICIENT_FUNDS("잔액이 부족합니다."),
    INVALID_DEPOSIT_AMOUNT("유효하지 않은 입금 금액입니다."),
    ACCOUNT_NOT_FOUND("계좌를 찾을 수 없습니다."),
    INVALID_INITIAL_BALANCE("초기 잔액 설정이 유효하지 않습니다."),
    INVALID_WITHDRAW_AMOUNT("유효하지 않은 출금 금액입니다.");

    private final String message;
}