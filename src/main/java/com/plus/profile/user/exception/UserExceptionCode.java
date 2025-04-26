package com.plus.profile.user.exception;

import com.plus.profile.global.exception.BaseExceptionCode;

public enum UserExceptionCode implements BaseExceptionCode {
    USER_NOT_FOUND(404, "User not found")

    private final int statusCode;
    private final String message;

    UserExceptionCode(int statusCode, String message) {
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
