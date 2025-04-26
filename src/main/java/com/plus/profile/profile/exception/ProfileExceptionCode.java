package com.plus.profile.profile.exception;

import com.plus.profile.global.error.BaseExceptionCode;
import lombok.Getter;

@Getter
public enum ProfileExceptionCode implements BaseExceptionCode {
    PROFILE_NOT_FOUND(404, "Profile not found");

    private final int statusCode;
    private final String message;

    ProfileExceptionCode(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
