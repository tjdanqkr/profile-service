package com.plus.profile.global.dto.point;

public enum PayOffResultType {
    SUCCESS("Payment success"),
    NOT_ENOUGH_POINTS("Not enough points"),
    COUPON_NOT_FOUND("Coupon not found"),
    COUPON_EXPIRED("Coupon expired"),
    COUPON_ALREADY_USED("Coupon already used"),
    INVALID_COUPON("Invalid coupon"),
    USER_NOT_FOUND("User not found"),
    SYSTEM_ERROR("Internal system error");

    private final String message;

    PayOffResultType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}