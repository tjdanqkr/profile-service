package com.plus.profile.point.exception;

import com.plus.profile.global.exception.BaseExceptionCode;
import lombok.Getter;

@Getter
public enum PointExceptionCode implements BaseExceptionCode {
    POINT_CHARGE_ARGUMENT_NOT_POSITIVE(400, "포인트 충전시 항상 충전 금액은 양수이어야 합니다.");

    private final int statusCode;
    private final String message;

    PointExceptionCode(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

}
