package com.plus.profile.global.exception;

import lombok.Getter;

@Getter
public enum GlobalPaymentException implements BaseExceptionCode{
    PAYMENT_CONFIRM_PENDING(400, "Payment confirm pending"),
    PAYMENT_CONFIRM_NOT_FOUND(404, "Payment confirm not found"),
    PAYMENT_CONFIRM_ALREADY(400, "Payment confirm already"),
    PAYMENT_CONFIRM_FAIL(400, "Payment confirm failed");
    private final int statusCode;
    private final String message;

    GlobalPaymentException(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
