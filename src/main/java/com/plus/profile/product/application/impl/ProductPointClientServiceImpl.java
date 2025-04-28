package com.plus.profile.product.application.impl;

import com.plus.profile.global.client.PointClientService;
import com.plus.profile.global.dto.point.PayOffPointRequest;
import com.plus.profile.global.dto.point.PayOffPointResponse;
import com.plus.profile.global.dto.point.PayOffPointWithCouponRequest;
import com.plus.profile.product.application.ProductPointClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductPointClientServiceImpl implements ProductPointClientService {
    private final PointClientService pointServiceImpl;
    @Override
    public PayOffPointResponse payOffPoint(PayOffPointRequest request) {
        return pointServiceImpl.payOffPoint(request);
    }

    @Override
    public PayOffPointResponse payOffPointWithCoupon(PayOffPointWithCouponRequest request) {
        return pointServiceImpl.payOffPointWithCoupon(request);
    }
}
