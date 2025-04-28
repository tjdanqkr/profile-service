package com.plus.profile.point.util;

import com.plus.profile.point.domain.UserCoupon;

public class Calculator {
    public static long calculateDiscount(UserCoupon userCoupon, long productPrice) {
        long discount = userCoupon.isCouponIsPercentage()
                ? userCoupon.getDiscountAmount() * productPrice / 100
                : userCoupon.getDiscountAmount();
        return Math.min(discount, 5_000L);
    }

}
