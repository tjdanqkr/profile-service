package com.plus.profile.product.application.impl;

import com.plus.profile.product.application.ProductPurchaseService;
import com.plus.profile.product.domain.repository.ProductRepository;
import com.plus.profile.product.presentation.dto.ProductPurchaseRequest;
import com.plus.profile.product.presentation.dto.ProductPurchaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductPurchaseServiceImpl implements ProductPurchaseService {
    private final ProductRepository productRepository;

    @Override
    public ProductPurchaseResponse productPurchase(UUID userId, Long productId) {
        return null;
    }

    @Override
    public ProductPurchaseResponse productPurchaseWithCoupon(UUID userId, ProductPurchaseRequest request) {
        return null;
    }
}
