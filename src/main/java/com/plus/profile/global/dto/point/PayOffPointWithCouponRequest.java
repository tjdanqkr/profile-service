package com.plus.profile.global.dto.point;

import java.util.UUID;

public record PayOffPointWithCouponRequest(
        UUID userId, long productId, long productPrice, String productName, long userCouponId
) {
}
