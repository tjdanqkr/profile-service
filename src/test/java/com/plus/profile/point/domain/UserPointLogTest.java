package com.plus.profile.point.domain;

import com.plus.profile.global.dto.point.PayOffPointRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserPointLogTest {



    @Test
    @DisplayName("포인트 충전 로그를 생성한다")
    void createChargeLog() {
        // given
        UUID userId = UUID.randomUUID();
        long before = 1000L;
        long after = 3000L;
        long pointsAmount = 2000L;

        // when
        UserPointLog log = UserPointLog.createChargeLog(userId, before, after, pointsAmount);

        // then
        assertThat(log.getUserId()).isEqualTo(userId);
        assertThat(log.getBeforePoints()).isEqualTo(before);
        assertThat(log.getAfterPoints()).isEqualTo(after);
        assertThat(log.getPointsAmount()).isEqualTo(pointsAmount);
        assertThat(log.getType()).isEqualTo(UserPointLogType.CHARGE);
    }




    @Test
    @DisplayName("쿠폰 없이 상품 결제 로그를 생성한다")
    void createPaidLog() {
        // given
        UUID userId = UUID.randomUUID();
        PayOffPointRequest request = new PayOffPointRequest(
                userId, 1L, 5000L, "상품1"
        );
        long before = 10000L;
        long after = 5000L;

        // when
        UserPointLog log = UserPointLog.createPaidLog(before, after, request);

        // then
        assertThat(log.getUserId()).isEqualTo(userId);
        assertThat(log.getProductId()).isEqualTo(1L);
        assertThat(log.getProductName()).isEqualTo("상품1");
        assertThat(log.getProductPrice()).isEqualTo(5000L);
        assertThat(log.getBeforePoints()).isEqualTo(before);
        assertThat(log.getAfterPoints()).isEqualTo(after);
        assertThat(log.getPointsAmount()).isEqualTo(5000L);
        assertThat(log.isCouponIsUsed()).isFalse();
        assertThat(log.getType()).isEqualTo(UserPointLogType.USE);
    }

    @Test
    @DisplayName("쿠폰 사용 상품 결제 로그를 생성한다")
    void createPaidWithCouponLog() {
        // given
        UUID userId = UUID.randomUUID();
        PayOffPointRequest request = new PayOffPointRequest(
                userId, 1L, 5000L, "상품1"
        );
        UserCoupon userCoupon = UserCoupon.builder()
                .id(10L)
                .couponId(UUID.randomUUID())
                .couponCode("ABC123")
                .description("테스트 쿠폰")
                .build();
        long before = 10000L;
        long after = 6000L;
        long discount = 4000L; // 실제 할인 금액

        // when
        UserPointLog log = UserPointLog.createPaidWhitCouponLog(before, after, discount, request, userCoupon);

        // then
        assertThat(log.getUserId()).isEqualTo(userId);
        assertThat(log.getProductId()).isEqualTo(1L);
        assertThat(log.getProductName()).isEqualTo("상품1");
        assertThat(log.getProductPrice()).isEqualTo(5000L);
        assertThat(log.getPointsAmount()).isEqualTo(5000L - discount);
        assertThat(log.getBeforePoints()).isEqualTo(before);
        assertThat(log.getAfterPoints()).isEqualTo(after);
        assertThat(log.getCouponId()).isEqualTo(userCoupon.getCouponId());
        assertThat(log.getUserCouponId()).isEqualTo(userCoupon.getId());
        assertThat(log.getCouponCode()).isEqualTo(userCoupon.getCouponCode());
        assertThat(log.getCouponDescription()).isEqualTo(userCoupon.getDescription());
        assertThat(log.getCouponDiscountAmount()).isEqualTo(discount);
        assertThat(log.isCouponIsUsed()).isTrue();
        assertThat(log.getType()).isEqualTo(UserPointLogType.USE);
    }
}
