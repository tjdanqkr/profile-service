package com.plus.profile.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plus.profile.product.domain.Product;
import com.plus.profile.user.domain.User;
import com.plus.profile.user.domain.UserRole;
import com.plus.profile.point.domain.UserPoint;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PointProductPurchaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager em;

    private UUID userId;
    private Long productId;

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
                .price(7_000L)
                .build();
        em.persist(product);

        productId = product.getId();

        em.flush();
        em.clear();
    }

    @AfterEach
    void tearDown() {
        em.createQuery("DELETE FROM UserPointLog").executeUpdate();
        em.createQuery("DELETE FROM Product").executeUpdate();
        em.createQuery("DELETE FROM UserPoint").executeUpdate();
        em.createQuery("DELETE FROM User").executeUpdate();
    }

    @Nested
    @DisplayName("상품 구매 플로우")
    class ProductPurchaseFlow {

        @Test
        @DisplayName("상품 구매 성공")
        void productPurchaseSuccess() throws Exception {
            // 1. 유저 포인트 조회
            mockMvc.perform(get("/api/v1/users/" + userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.point").value(10_000));

            // 2. 상품 구매 요청
            mockMvc.perform(post("/api/v1/products/" + productId + "/purchase")
                            .param("userId", userId.toString())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            // 3. 유저 포인트 확인 (10,000 - 7,000 = 3,000)
            mockMvc.perform(get("/api/v1/users/" + userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.point").value(3_000));
        }

        @Test
        @DisplayName("포인트 부족으로 상품 구매 실패")
        void productPurchaseFailDueToInsufficientPoints() throws Exception {
            // 유저 생성
            User user = User.builder()
                    .username("testuser1")
                    .encodedPassword("password1")
                    .role(UserRole.USER)
                    .build();
            em.persist(user);

            em.persist(UserPoint.builder()
                    .point(5_000L)
                    .user(user)
                    .build()
            );
            em.flush();
            em.clear();
            userId = user.getId();

            // 상품 생성
            Product product = Product.builder()
                    .name("테스트 상품")
                    .price(7_000L)
                    .build();
            em.persist(product);

            // 1. 유저 포인트 조회
            mockMvc.perform(get("/api/v1/users/" + userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.point").value(5_000));

            // 2. 상품 구매 요청
            mockMvc.perform(post("/api/v1/products/" + productId + "/purchase")
                            .param("userId", userId.toString())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Not enough points"));
        }
    }
}
