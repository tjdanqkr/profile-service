package com.plus.profile.product.presentation.dto;

public record ProductPurchaseResponse(
        long productId,
        String productName,
        long originalPrice,
        long discountAmount,
        long finalPrice,
        long remainingPoints
) {
}
