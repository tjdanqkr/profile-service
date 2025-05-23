package com.plus.profile.user.presentation.dto;


import com.plus.profile.point.domain.UserCoupon;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class UserCouponResponse {
    private final Long id;
    private final UUID couponId;
    private final String couponCode;
    private final long discountAmount;
    private final LocalDateTime expirationDate;
    private final String description;

    public UserCouponResponse(Long id, UUID couponId, String couponCode, long discountAmount, LocalDateTime expirationDate, String description) {
        this.id = id;
        this.couponId = couponId;
        this.couponCode = couponCode;
        this.discountAmount = discountAmount;
        this.expirationDate = expirationDate;
        this.description = description;
    }

    @QueryProjection
    public UserCouponResponse(UserCoupon userCoupon) {
        this.id = userCoupon.getId();
        this.couponId = userCoupon.getCouponId();
        this.couponCode = userCoupon.getCouponCode();
        this.discountAmount = userCoupon.getDiscountAmount();
        this.expirationDate = userCoupon.getExpirationDate();
        this.description = userCoupon.getDescription();
    }
}
