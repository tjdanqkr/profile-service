package com.plus.profile.global.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {
    private final boolean success;
    private final String message;
    private final int statusCode;
    private final List<FieldError> errors;
    private final String timestamp = LocalDateTime.now().toString();
    @Getter
    @Builder
    public static class FieldError {
        private final String field;
        private final String rejectedValue;
        private final String reason;

        public static FieldError of(String field, Object rejectedValue, String reason) {
            return FieldError.builder()
                    .field(field)
                    .rejectedValue(rejectedValue == null ? "null" : rejectedValue.toString())
                    .reason(reason)
                    .build();
        }
    }
    public static ErrorResponse of(String message, int statusCode) {
        return new ErrorResponse(false, message, statusCode, null);
    }
    public static ErrorResponse of(String message, int statusCode, List<FieldError> errors) {
        return new ErrorResponse(false, message, statusCode, errors);
    }
}
