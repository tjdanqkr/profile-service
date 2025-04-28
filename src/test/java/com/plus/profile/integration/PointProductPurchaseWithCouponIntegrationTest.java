
package com.plus.profile.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plus.profile.point.domain.UserCoupon;
import com.plus.profile.point.domain.UserPoint;
import com.plus.profile.product.domain.Product;
import com.plus.profile.product.presentation.dto.ProductPurchaseRequest;
import com.plus.profile.user.domain.User;
import com.plus.profile.user.domain.UserRole;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PointProductPurchaseWithCouponIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager em;

    private UUID userId;
    private Long productId;
    private Long percentCouponId;
    private Long amountCouponId;

    @BeforeEach
    void setUp() {
        // 유저 생성
        User user = User.builder()
                .username("testuser")
                .encodedPassword("password")
                .role(UserRole.USER)
                .build();
        em.persist(user);

        em.persist(UserPoint.builder()
                .point(10_000L)
                .user(user)
                .build()
        );

        userId = user.getId();

        // 상품 생성
        Product product = Product.builder()
                .name("테스트 상품")
                .price(8_000L)
                .build();
        em.persist(product);

        productId = product.getId();

        // 퍼센트 할인 쿠폰 (20% 할인, 최대 5,000원 할인)
        UserCoupon percentCoupon = UserCoupon.builder()
                .user(user)
                .couponId(UUID.randomUUID())
                .couponCode("PERCENT_COUPON")
                .couponIsPercentage(true)
                .discountAmount(20)
                .description("20% 할인 쿠폰")
                .expirationDate(LocalDateTime.now().plusDays(30))
                .build();
        em.persist(percentCoupon);
        percentCouponId = percentCoupon.getId();

        // 고정 금액 할인 쿠폰 (3,000P 할인)
        UserCoupon amountCoupon = UserCoupon.builder()
                .user(user)
                .couponId(UUID.randomUUID())
                .couponIsPercentage(false)
                .couponCode("AMOUNT_COUPON")
                .discountAmount(3_000)
                .description("3,000원 할인 쿠폰")
                .expirationDate(LocalDateTime.now().plusDays(30))
                .build();
        em.persist(amountCoupon);
        amountCouponId = amountCoupon.getId();

        em.flush();
        em.clear();
    }

    @AfterEach
    void tearDown() {
        em.createQuery("DELETE FROM UserPointLog").executeUpdate();
        em.createQuery("DELETE FROM UserCoupon").executeUpdate();
        em.createQuery("DELETE FROM Product").executeUpdate();
        em.createQuery("DELETE FROM UserPoint").executeUpdate();
        em.createQuery("DELETE FROM User").executeUpdate();
    }

    @Nested
    @DisplayName("쿠폰 적용 상품 구매 플로우")
    class CouponPurchaseFlow {

        @Test
        @DisplayName("퍼센트 할인 쿠폰 적용 구매 성공")
        void purchaseWithPercentCoupon() throws Exception {
            // 8,000원 상품에 20% 할인 (1,600P 할인), 최종 6,400P
            ProductPurchaseRequest request = new ProductPurchaseRequest(percentCouponId);
            mockMvc.perform(post("/api/v1/products/" + productId + "/purchase-with-coupon")
                            .param("userId", userId.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.finalPrice").value(6400))
                    .andExpect(jsonPath("$.data.discountAmount").value(1600));

            // 포인트 확인 (10,000 - 6,400 = 3,600P)
            mockMvc.perform(get("/api/v1/users/" + userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.point").value(3600));
        }

        @Test
        @DisplayName("최대 5000원 할인 적용 구매 성공")
        void purchaseWithPercentCouponMax5000() throws Exception {
            User user = User.builder()
                    .username("testuser1")
                    .encodedPassword("password")
                    .role(UserRole.USER)
                    .build();
            em.persist(user);

            em.persist(UserPoint.builder()
                    .point(10_000L)
                    .user(user)
                    .build()
            );

            userId = user.getId();
            // 상품 생성
            Product product = Product.builder()
                    .name("테스트 상품")
                    .price(10_000L)
                    .build();
            em.persist(product);

            productId = product.getId();
            em.flush();
            em.clear();

            // 퍼센트 할인 쿠폰 (70% 할인, 최대 5,000원 할인)
            UserCoupon percentCoupon = UserCoupon.builder()
                    .user(user)
                    .couponId(UUID.randomUUID())
                    .couponCode("PERCENT_COUPON")
                    .couponIsPercentage(true)
                    .discountAmount(70)
                    .description("70% 할인 쿠폰")
                    .expirationDate(LocalDateTime.now().plusDays(30))
                    .build();
            em.persist(percentCoupon);
            percentCouponId = percentCoupon.getId();

            // 10000원 상품에 70% 할인 (7,000P 할인), 최종 5,000P
            ProductPurchaseRequest request = new ProductPurchaseRequest(percentCouponId);
            mockMvc.perform(post("/api/v1/products/" + productId + "/purchase-with-coupon")
                            .param("userId", userId.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.finalPrice").value(5000))
                    .andExpect(jsonPath("$.data.discountAmount").value(5000));

            // 포인트 확인 (10,000 - 5,000 = 5,000P)
            mockMvc.perform(get("/api/v1/users/" + userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.point").value(5000));
        }

        @Test
        @DisplayName("고정 금액 할인 쿠폰 적용 구매 성공")
        void purchaseWithAmountCoupon() throws Exception {
            // 8,000원 상품에 3,000P 할인, 최종 5,000P
            ProductPurchaseRequest request = new ProductPurchaseRequest(amountCouponId);
            mockMvc.perform(post("/api/v1/products/" + productId + "/purchase-with-coupon")
                            .param("userId", userId.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.finalPrice").value(5000))
                    .andExpect(jsonPath("$.data.discountAmount").value(3000));

            // 포인트 확인 (10,000 - 5,000 = 5,000P)
            mockMvc.perform(get("/api/v1/users/" + userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.point").value(5000));
        }

        @Test
        @DisplayName("포인트 부족 + 쿠폰 적용해도 구매 실패")
        void purchaseFailDueToNotEnoughPoint() throws Exception {
            User user = User.builder()
                    .username("testuser1")
                    .encodedPassword("password")
                    .role(UserRole.USER)
                    .build();
            em.persist(user);

            em.persist(UserPoint.builder()
                    .point(1_000L)
                    .user(user)
                    .build()
            );

            userId = user.getId();
            em.flush();
            em.clear();

            ProductPurchaseRequest request = new ProductPurchaseRequest(percentCouponId);

            // 8,000 - 1,600 = 6,400P 필요. (현재 1,000P이므로 실패해야 함)
            mockMvc.perform(post("/api/v1/products/" + productId + "/purchase-with-coupon")
                            .param("userId", userId.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Not enough points"));
        }
    }
}
