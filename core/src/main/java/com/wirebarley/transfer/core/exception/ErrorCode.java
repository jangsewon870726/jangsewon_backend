package com.wirebarley.transfer.core.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INSUFFICIENT_FUNDS("잔액이 부족합니다."),
    INVALID_DEPOSIT_AMOUNT("유효하지 않은 입금 금액입니다."),
    ACCOUNT_NOT_FOUND("계좌를 찾을 수 없습니다."),
    INVALID_INITIAL_BALANCE("초기 잔액 설정이 유효하지 않습니다."),
    INVALID_WITHDRAW_AMOUNT("유효하지 않은 출금 금액입니다."),
    SELF_TRANSFER_NOT_ALLOWED("자기 자신에게 송금할 수 없습니다."),
    ACCOUNT_NUMBER_ALREADY_EXISTS("이미 존재하는 계좌번호입니다."),
    BALANCE_NOT_ZERO("잔액이 0원이 아닌 계좌는 삭제할 수 없습니다."),
    DAILY_LIMIT_EXCEEDED("1일 이체 한도(300만원)를 초과했습니다."),
    WITHDRAW_DAILY_LIMIT_EXCEEDED("1일 출금 한도(100만원)를 초과했습니다."),
    INVALID_ACCOUNT_PASSWORD("계좌 비밀번호가 일치하지 않습니다.");

    private final String message;
}