package com.plus.profile.user.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plus.profile.global.dto.ApiResponse;
import com.plus.profile.global.dto.CreatePaymentResponse;
import com.plus.profile.global.dto.PayGatewayCompany;
import com.plus.profile.global.exception.BusinessException;
import com.plus.profile.user.application.PointService;
import com.plus.profile.user.application.UserService;
import com.plus.profile.user.exception.UserExceptionCode;
import com.plus.profile.user.presentation.dto.PointChargeConfirmRequest;
import com.plus.profile.user.presentation.dto.PointChargeRequest;
import com.plus.profile.user.presentation.dto.UserDetailResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private PointService pointService;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("GET /api/v1/users/{userId}")
    class GetUserDetail {

        @Test
        @DisplayName("정상적으로 유저 디테일을 조회한다")
        void shouldReturnUserDetailSuccessfully() throws Exception {
            // given
            UUID userId = UUID.randomUUID();
            UserDetailResponse mockResponse = new UserDetailResponse(
                    userId,
                    "testuser",
                    "USER",
                    10000L,
                    Collections.emptyList()
            );

            given(userService.getUserDetail(userId))
                    .willReturn(mockResponse);

            // when & then
            mockMvc.perform(get("/api/v1/users/{userId}", userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(userId.toString()))
                    .andExpect(jsonPath("$.data.username").value("testuser"))
                    .andExpect(jsonPath("$.data.role").value("USER"))
                    .andExpect(jsonPath("$.data.point").value(10000));
        }
        @Test
        @DisplayName("존재하지 않는 유저를 조회할 때 404 에러를 반환한다")
        void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
            // given
            UUID userId = UUID.randomUUID();
            given(userService.getUserDetail(userId))
                    .willThrow(new BusinessException(UserExceptionCode.USER_NOT_FOUND));

            // when & then
            mockMvc.perform(get("/api/v1/users/{userId}", userId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("User not found"));
        }
    }
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

            doThrow(new RuntimeException("결제 검증 실패"))
                    .when(pointService)
                    .confirmPointCharge(userId, orderId);

            PointChargeConfirmRequest request = new PointChargeConfirmRequest(orderId);

            // when & then
            mockMvc.perform(post("/api/v1/users/{userId}/points/confirm", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError());
        }
    }
}
