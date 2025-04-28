package com.plus.profile.product.exception;

import com.plus.profile.global.exception.BaseExceptionCode;
import lombok.Getter;

@Getter
public enum ProductExceptionCode implements BaseExceptionCode {
    COUPON_NOT_FOUND(400,"Coupon not found"),
    COUPON_EXPIRED(400,"Coupon expired"),
    COUPON_ALREADY_USED(400,"Coupon already used"),
    INVALID_COUPON(400,"Invalid coupon"),
    USER_NOT_FOUND(401,"User not found"),
    SYSTEM_ERROR(500,"Internal system error"),
    NOT_ENOUGH_POINTS(400, "Not enough point"),
    PRODUCT_NOT_FOUND(404, "Product not found");

    private final int statusCode;
    private final String message;

    ProductExceptionCode(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

}
