package com.plus.profile.product.application;

import com.plus.profile.product.presentation.dto.ProductPurchaseRequest;
import com.plus.profile.product.presentation.dto.ProductPurchaseResponse;

import java.util.UUID;

public interface ProductPurchaseService {
    ProductPurchaseResponse productPurchase(UUID userId, Long productId);
    ProductPurchaseResponse productPurchaseWithCoupon(UUID userId, ProductPurchaseRequest request);
}
