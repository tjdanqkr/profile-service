package com.plus.profile.product.application.impl;

import com.plus.profile.global.dto.point.PayOffPointRequest;
import com.plus.profile.global.dto.point.PayOffPointResponse;
import com.plus.profile.global.dto.point.PayOffPointWithCouponRequest;
import com.plus.profile.global.dto.point.PayOffResultType;
import com.plus.profile.global.exception.BusinessException;
import com.plus.profile.product.application.ProductPointClientService;
import com.plus.profile.product.application.ProductPurchaseService;
import com.plus.profile.product.domain.Product;
import com.plus.profile.product.domain.repository.ProductRepository;
import com.plus.profile.product.exception.ProductExceptionCode;
import com.plus.profile.product.presentation.dto.ProductPurchaseRequest;
import com.plus.profile.product.presentation.dto.ProductPurchaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductPurchaseServiceImpl implements ProductPurchaseService {
    private final ProductRepository productRepository;
    private final ProductPointClientService productPointClientService;

    @Override
    public ProductPurchaseResponse productPurchase(UUID userId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ProductExceptionCode.PRODUCT_NOT_FOUND));

        PayOffPointRequest request = new PayOffPointRequest(userId, product.getPrice(), product.getPrice(), product.getName());
        PayOffPointResponse response = productPointClientService.payOffPoint(request);
        if(response.resultType().equals(PayOffResultType.USER_NOT_FOUND))
            throw new BusinessException(ProductExceptionCode.USER_NOT_FOUND);
        if(response.resultType().equals(PayOffResultType.NOT_ENOUGH_POINTS))
            throw new BusinessException(ProductExceptionCode.NOT_ENOUGH_POINTS);
        if(response.resultType().equals(PayOffResultType.SYSTEM_ERROR))
            throw new BusinessException(ProductExceptionCode.SYSTEM_ERROR);
        return new ProductPurchaseResponse(productId, product.getName(), product.getPrice(), 0, product.getPrice(), response.remainPoint());
    }

    @Override
    public ProductPurchaseResponse productPurchaseWithCoupon(UUID userId, Long productId, ProductPurchaseRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ProductExceptionCode.PRODUCT_NOT_FOUND));

        PayOffPointWithCouponRequest payRequest = new PayOffPointWithCouponRequest(
                userId,
                product.getId(),
                product.getPrice(),
                product.getName(),
                request.userCouponId()
        );

        PayOffPointResponse response = productPointClientService.payOffPointWithCoupon(payRequest);

        if (response.resultType().equals(PayOffResultType.USER_NOT_FOUND)) {
            throw new BusinessException(ProductExceptionCode.USER_NOT_FOUND);
        }
        if (response.resultType().equals(PayOffResultType.NOT_ENOUGH_POINTS)) {
            throw new BusinessException(ProductExceptionCode.NOT_ENOUGH_POINTS);
        }
        if (response.resultType().equals(PayOffResultType.COUPON_NOT_FOUND)) {
            throw new BusinessException(ProductExceptionCode.COUPON_NOT_FOUND);
        }
        if (response.resultType().equals(PayOffResultType.COUPON_ALREADY_USED)) {
            throw new BusinessException(ProductExceptionCode.COUPON_ALREADY_USED);
        }
        if (response.resultType().equals(PayOffResultType.COUPON_EXPIRED)) {
            throw new BusinessException(ProductExceptionCode.COUPON_EXPIRED);
        }
        if (response.resultType().equals(PayOffResultType.SYSTEM_ERROR)) {
            throw new BusinessException(ProductExceptionCode.SYSTEM_ERROR);
        }

        long discountAmount = product.getPrice() - response.usedPoint();

        return new ProductPurchaseResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                discountAmount,
                response.usedPoint(),
                response.remainPoint()
        );
    }
}
