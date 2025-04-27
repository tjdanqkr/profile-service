package com.plus.profile.user.infra.impl;

import com.plus.profile.user.domain.User;
import com.plus.profile.point.domain.UserCoupon;
import com.plus.profile.point.domain.UserPoint;
import com.plus.profile.user.domain.UserRole;
import com.plus.profile.user.presentation.dto.UserDetailResponse;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class UserRepositoryCustomImplTest {

    @Autowired
    private EntityManager em;
    @Autowired
    private UserRepositoryCustomImpl userRepositoryCustomImpl;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .username("testuser")
                .encodedPassword("password")
                .role(UserRole.USER)
                .build();
        em.persist(user);
        userId = user.getId();
        UserPoint userPoint = UserPoint.builder()
                .point(10000L)
                .user(user)
                .build();
        em.persist(userPoint);

        UserCoupon coupon1 = UserCoupon.builder()
                .couponId(UUID.randomUUID())
                .couponCode("COUPON10")
                .discountAmount(1000L)
                .expirationDate(LocalDateTime.now().plusDays(30))
                .description("10% 할인쿠폰")
                .isUsed(false)
                .user(user)
                .build();

        UserCoupon coupon2 = UserCoupon.builder()
                .couponId(UUID.randomUUID())
                .couponCode("COUPON20")
                .discountAmount(2000L)
                .expirationDate(LocalDateTime.now().plusDays(30))
                .description("20% 할인쿠폰")
                .isUsed(false)
                .user(user)
                .build();

        em.persist(coupon1);
        em.persist(coupon2);

        em.flush();
        em.clear();
    }

    @Nested
    @DisplayName("findUserDetailById 메서드 테스트")
    class FindUserDetailById {

        @Test
        @DisplayName("존재하는 유저를 정상 조회한다")
        void shouldReturnUserDetailSuccessfully() {
            // when
            UserDetailResponse response = userRepositoryCustomImpl.findUserDetailById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            // then
            assertThat(response.getId()).isEqualTo(userId);
            assertThat(response.getUsername()).isEqualTo("testuser");
            assertThat(response.getRole()).isEqualTo("ROLE_USER");
            assertThat(response.getPoint()).isEqualTo(10000L);
            assertThat(response.getCoupons()).hasSize(2);
            assertThat(response.getCoupons()).extracting("couponCode")
                    .containsExactlyInAnyOrder("COUPON10", "COUPON20");
        }

        @Test
        @DisplayName("존재하지 않는 유저 조회 시 empty 반환")
        void shouldReturnEmptyWhenUserNotFound() {
            // given
            UUID nonExistentId = UUID.randomUUID();

            // when
            var result = userRepositoryCustomImpl.findUserDetailById(nonExistentId);

            // then
            assertThat(result).isNotPresent();
        }
    }
}
