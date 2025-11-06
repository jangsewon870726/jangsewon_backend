package com.wirebarley.transfer.core.exception;

public class InvalidWithdrawAmountException extends BusinessException {

    public InvalidWithdrawAmountException() {
        super(ErrorCode.INVALID_WITHDRAW_AMOUNT);
    }
}