package com.wirebarley.transfer.core.exception;

public class InvalidDepositAmountException extends BusinessException {

    public InvalidDepositAmountException() {
        super(ErrorCode.INVALID_DEPOSIT_AMOUNT);
    }
}