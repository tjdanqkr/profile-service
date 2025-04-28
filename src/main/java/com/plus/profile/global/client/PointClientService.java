package com.plus.profile.global.client;

import com.plus.profile.global.dto.point.PayOffPointRequest;
import com.plus.profile.global.dto.point.PayOffPointResponse;
import com.plus.profile.global.dto.point.PayOffPointWithCouponRequest;

public interface PointClientService {
    PayOffPointResponse payOffPoint(PayOffPointRequest request);
    PayOffPointResponse payOffPointWithCoupon(PayOffPointWithCouponRequest request);
}
