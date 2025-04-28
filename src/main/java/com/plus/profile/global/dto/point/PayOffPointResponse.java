package com.plus.profile.global.dto.point;

public record PayOffPointResponse(
        boolean isSuccess,
        String message,
        PayOffResultType resultType,
        long remainPoint
) {
}
