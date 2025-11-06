package com.wirebarley.transfer.core.exception;

public class InvalidInitialBalanceException extends BusinessException {

    public InvalidInitialBalanceException() {
        super(ErrorCode.INVALID_INITIAL_BALANCE);
    }
}