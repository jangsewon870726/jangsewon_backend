package com.wirebarley.transfer.api.exception;

import com.wirebarley.transfer.core.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ErrorResponse {

    private final String code;
    private final String message;

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.name(), errorCode.getMessage());
    }

    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(code, message);
    }

    private ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }
}