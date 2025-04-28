package com.plus.profile.point.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserCouponTest {

    @Test
    @DisplayName("쿠폰을 사용한다")
    void useCoupon() {
        // given
        UserCoupon userCoupon = UserCoupon.builder()
                .isUsed(false)
                .build();

        // when
        userCoupon.useCoupon();

        // then
        assertTrue(userCoupon.isUsed());
    }
}