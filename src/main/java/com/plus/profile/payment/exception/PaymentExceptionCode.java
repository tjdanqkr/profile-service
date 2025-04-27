package com.plus.profile.payment.exception;

import com.plus.profile.global.exception.BaseExceptionCode;

public enum PaymentExceptionCode implements BaseExceptionCode {
    PG_SERVICE_ERROR(500, "PG service error"),
    PAYMENT_NOT_FOUND(404, "Payment transaction not found");
    private final int statusCode;
    private final String message;

    PaymentExceptionCode(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getMessage() {
        return message;
    }


}
