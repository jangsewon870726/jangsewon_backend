package com.wirebarley.transfer.core.exception;

public class InsufficientFundsException extends BusinessException {

    public InsufficientFundsException() {
        super(ErrorCode.INSUFFICIENT_FUNDS);
    }
}