package com.plus.profile.global.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final int statusCode;
    private final String message;
    public BusinessException(BaseExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.statusCode = exceptionCode.getStatusCode();
        this.message = exceptionCode.getMessage();
    }
}
