package com.plus.profile.product.presentation;

import com.plus.profile.global.dto.ApiResponse;
import com.plus.profile.product.application.ProductPurchaseService;
import com.plus.profile.product.presentation.dto.ProductPurchaseRequest;
import com.plus.profile.product.presentation.dto.ProductPurchaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductPurchaseService productPurchaseService;

    @PostMapping("/api/v1/products/{productId}/purchase")
    public ApiResponse<ProductPurchaseResponse> purchaseProduct(
            @PathVariable Long productId,
            @RequestParam UUID userId
    ) {
        ProductPurchaseResponse response = productPurchaseService.productPurchase(userId, productId);
        return ApiResponse.success(response);
    }

    @PostMapping("/api/v1/products/{productId}/purchase-with-coupon")
    public ApiResponse<ProductPurchaseResponse> purchaseProductWithCoupon(
            @RequestParam UUID userId,
            @PathVariable Long productId,
            @RequestBody @Valid ProductPurchaseRequest request
    ) {
        ProductPurchaseResponse response = productPurchaseService.productPurchaseWithCoupon(userId, productId, request);
        return ApiResponse.success(response);
    }
}
