package com.plus.profile.product.presentation.dto;


import jakarta.validation.constraints.NotNull;

public record ProductPurchaseRequest(
        @NotNull(message = "Coupon ID is required when using a coupon.")
        Long userCouponId
) {
}
