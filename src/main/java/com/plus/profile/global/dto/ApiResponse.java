package com.plus.profile.global.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {
    private final boolean success;
    private final T data;
    private final int statusCode;
    private final String timestamp = LocalDateTime.now().toString();

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, 200);
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(true, data, 201);
    }

}
