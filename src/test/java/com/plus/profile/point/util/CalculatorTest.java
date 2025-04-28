package com.plus.profile.point.util;

import com.plus.profile.point.domain.UserCoupon;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CalculatorTest {

    @Nested
    @DisplayName("calculateDiscount 메서드 테스트")
    class CalculateDiscountTest {

        @Test
        @DisplayName("정액 쿠폰 할인 계산 성공 - 최대치 미만")
        void fixedAmountCouponDiscountBelowMax() {
            // given
            UserCoupon coupon = UserCoupon.builder()
                    .id(1L)
                    .couponIsPercentage(false)
                    .discountAmount(3000L)
                    .expirationDate(LocalDateTime.now().plusDays(1))
                    .build();

            // when
            long discount = Calculator.calculateDiscount(coupon, 10000L);

            // then
            assertThat(discount).isEqualTo(3000L);
        }

        @Test
        @DisplayName("정액 쿠폰 할인 계산 성공 - 최대치 초과")
        void fixedAmountCouponDiscountAboveMax() {
            // given
            UserCoupon coupon = UserCoupon.builder()
                    .id(1L)
                    .couponIsPercentage(false)
                    .discountAmount(10000L)
                    .expirationDate(LocalDateTime.now().plusDays(1))
                    .build();

            // when
            long discount = Calculator.calculateDiscount(coupon, 10000L);

            // then
            assertThat(discount).isEqualTo(5000L);
        }

        @Test
        @DisplayName("퍼센트 쿠폰 할인 계산 성공 - 최대치 미만")
        void percentageCouponDiscountBelowMax() {
            // given
            UserCoupon coupon = UserCoupon.builder()
                    .id(1L)
                    .couponIsPercentage(true)
                    .discountAmount(10L) // 10%
                    .expirationDate(LocalDateTime.now().plusDays(1))
                    .build();

            // when
            long discount = Calculator.calculateDiscount(coupon, 20000L);

            // then
            assertThat(discount).isEqualTo(2000L);
        }

        @Test
        @DisplayName("퍼센트 쿠폰 할인 계산 성공 - 최대치 초과")
        void percentageCouponDiscountAboveMax() {
            // given
            UserCoupon coupon = UserCoupon.builder()
                    .id(1L)
                    .couponIsPercentage(true)
                    .discountAmount(60L) // 60%
                    .expirationDate(LocalDateTime.now().plusDays(1))
                    .build();

            // when
            long discount = Calculator.calculateDiscount(coupon, 10000L);

            // then
            assertThat(discount).isEqualTo(5000L);
        }
    }
}
