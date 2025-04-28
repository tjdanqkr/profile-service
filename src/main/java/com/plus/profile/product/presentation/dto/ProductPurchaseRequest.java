package com.plus.profile.product.presentation.dto;

public record ProductPurchaseRequest(
        Long productId,
        Long userCouponId //nullable
) {
}
