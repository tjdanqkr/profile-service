package com.plus.profile.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plus.profile.global.dto.CreatePaymentResponse;
import com.plus.profile.global.dto.PayGatewayCompany;
import com.plus.profile.point.domain.UserPoint;
import com.plus.profile.point.presentation.dto.PointChargeConfirmRequest;
import com.plus.profile.point.presentation.dto.PointChargeRequest;
import com.plus.profile.user.domain.User;
import com.plus.profile.user.domain.UserRole;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
class PointChargeIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private EntityManager em;

    private UUID userId;

    @BeforeEach
    void setUp() {
        // 유저 생성
        User user = User.builder()
                .username("testuser")
                .encodedPassword("password")
                .role(UserRole.USER)
                .build();
        em.persist(user);
        em.persist(
                UserPoint.builder()
                        .point(0L)
                        .user(user)
                        .build()
        );

        userId = user.getId();
        em.flush();
        em.clear();
        System.out.println("User ID: " + userId);
    }
    @AfterEach
    void tearDown() {

        em.createQuery("DELETE FROM UserPointLog").executeUpdate();
        em.createQuery("DELETE FROM UserPoint").executeUpdate();
        em.createQuery("DELETE FROM User").executeUpdate();

    }

    @Test
    @DisplayName("포인트 충전 플로우 통합 테스트")
    void pointChargeFlow() throws Exception {
        // 1. 유저 조회 (GET /users/{userId})
        mockMvc.perform(get("/api/v1/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(userId.toString()));

        // 2. 포인트 충전 요청 (POST /users/{userId}/points)
        PointChargeRequest pointChargeRequest = new PointChargeRequest(500L, PayGatewayCompany.TOSS, "abc");

        String pointChargeResult = mockMvc.perform(post("/api/v1/users/" + userId + "/points")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pointChargeRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CreatePaymentResponse paymentResponse = objectMapper.readValue(
                objectMapper.readTree(pointChargeResult).path("data").toString(),
                CreatePaymentResponse.class
        );
        UUID orderId = paymentResponse.orderId();

        // 3. 결제 콜백 호출 (GET /payments/toss/callback)
        mockMvc.perform(get("/api/v1/payments/toss/callback")
                        .param("paymentKey", "test-payment-key")
                        .param("orderId", orderId.toString())
                        .param("amount", "500"))
                .andExpect(status().isOk());

        // 4. 포인트 충전 확정 (POST /users/{userId}/points/confirm)
        PointChargeConfirmRequest confirmRequest = new PointChargeConfirmRequest(orderId);

        mockMvc.perform(post("/api/v1/users/" + userId + "/points/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(confirmRequest)))
                .andExpect(status().isOk());

        // 5. 다시 유저 조회해서 포인트 확인
        mockMvc.perform(get("/api/v1/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.point").value(500));
    }
}
