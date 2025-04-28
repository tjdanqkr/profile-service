package com.plus.profile.point.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plus.profile.global.dto.payment.CreatePaymentResponse;
import com.plus.profile.global.dto.PayGatewayCompany;
import com.plus.profile.global.exception.BusinessException;
import com.plus.profile.global.exception.GlobalPaymentException;
import com.plus.profile.point.application.PointService;
import com.plus.profile.point.presentation.dto.PointChargeConfirmRequest;
import com.plus.profile.point.presentation.dto.PointChargeRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PointController.class)
class PointControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PointService pointService;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("포인트 충전 API 테스트")
    class ChargePoint {

        @Test
        @DisplayName("포인트 충전 성공")
        void shouldChargePoint() throws Exception {
            // given
            UUID userId = UUID.randomUUID();
            PointChargeRequest request = new PointChargeRequest(1000L, PayGatewayCompany.TOSS, "test-support-key");

            CreatePaymentResponse response = new CreatePaymentResponse(
                    UUID.randomUUID(),
                    "[포인트 충전] 1000 포인트 충전합니다.",
                    1000L,
                    "PENDING",
                    "TOSS"
            );

            when(pointService.chargePoint(any(UUID.class), any(PointChargeRequest.class)))
                    .thenReturn(response);

            // when & then
            mockMvc.perform(post("/api/v1/users/{userId}/points", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.transactionAmount").value(1000L))
                    .andExpect(jsonPath("$.data.pgType").value("TOSS"));
        }

        @Test
        @DisplayName("포인트 충전 실패 - 최소 금액 미만")
        void shouldFailChargePointWhenAmountIsTooSmall() throws Exception {
            // given
            UUID userId = UUID.randomUUID();
            PointChargeRequest request = new PointChargeRequest(50L, PayGatewayCompany.TOSS,"test-support-key"); // 100 미만

            // when & then
            mockMvc.perform(post("/api/v1/users/{userId}/points", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }
    @Nested
    @DisplayName("POST /api/v1/users/{userId}/points/confirm")
    class ConfirmPointChargeTest {

        @Test
        @DisplayName("포인트 충전 확정 성공")
        void confirmPointChargeSuccess() throws Exception {
            // given
            UUID userId = UUID.randomUUID();
            UUID orderId = UUID.randomUUID();

            doNothing().when(pointService).confirmPointCharge(userId, orderId);

            PointChargeConfirmRequest request = new PointChargeConfirmRequest(orderId);

            // when & then
            mockMvc.perform(post("/api/v1/users/{userId}/points/confirm", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.statusCode").value(200))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").doesNotExist());
        }

        @Test
        @DisplayName("포인트 충전 확정 실패 (BusinessException)")
        void confirmPointChargeFail() throws Exception {
            // given
            UUID userId = UUID.randomUUID();
            UUID orderId = UUID.randomUUID();

            doThrow(new BusinessException(GlobalPaymentException.PAYMENT_CONFIRM_FAIL))
                    .when(pointService)
                    .confirmPointCharge(userId, orderId);

            PointChargeConfirmRequest request = new PointChargeConfirmRequest(orderId);

            // when & then
            mockMvc.perform(post("/api/v1/users/{userId}/points/confirm", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.statusCode").value(400))
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Payment confirm failed"));
        }
    }
}