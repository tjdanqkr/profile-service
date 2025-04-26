package com.plus.profile.global.exception;

import lombok.Getter;

@Getter
public enum GlobalServerException implements BaseExceptionCode {
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private final int statusCode;
    private final String message;

    GlobalServerException(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
