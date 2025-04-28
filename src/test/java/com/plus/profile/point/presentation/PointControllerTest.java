package com.plus.profile.point.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plus.profile.global.dto.PayGatewayCompany;
import com.plus.profile.global.dto.payment.CreatePaymentResponse;
import com.plus.profile.global.exception.BusinessException;
import com.plus.profile.global.exception.GlobalPaymentException;
import com.plus.profile.point.application.PointService;
import com.plus.profile.point.presentation.dto.PointChargeConfirmRequest;
import com.plus.profile.point.presentation.dto.PointChargeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(PointController.class)
@AutoConfigureRestDocs
class PointControllerTest {

    private MockMvc mockMvc;

    @MockitoBean
    private PointService pointService;

    @Autowired
    private ObjectMapper objectMapper;
    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
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

            when(pointService.chargePoint(userId, request))
                    .thenReturn(response);

            // when & then
            mockMvc.perform(post("/api/v1/users/{userId}/points", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.statusCode").value(200))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.timestamp").isNotEmpty())
                    .andExpect(jsonPath("$.data.transactionAmount").value(1000L))
                    .andExpect(jsonPath("$.data.pgType").value("TOSS"))
                    .andExpect(jsonPath("$.data.orderId").isNotEmpty())
                    .andExpect(jsonPath("$.data.orderName").value("[포인트 충전] 1000 포인트 충전합니다."))
                    .andExpect(jsonPath("$.data.transactionStatus").value("PENDING"))
                    .andDo(document("point-charge",
                            requestFields(
                                    fieldWithPath("amount").description("충전할 포인트 금액"),
                                    fieldWithPath("pgType").description("PG사"),
                                    fieldWithPath("supportKey").description("결제 키")
                            ),
                            responseFields(
                                    fieldWithPath("success").description("요청 성공 여부"),
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("timestamp").description("요청 시각"),
                                    fieldWithPath("data.transactionAmount").description("결제 금액"),
                                    fieldWithPath("data.pgType").description("PG사"),
                                    fieldWithPath("data.transactionStatus").description("결제 상태"),
                                    fieldWithPath("data.orderName").description("주문 이름"),
                                    fieldWithPath("data.orderId").description("주문 ID")
                            )
                    ));
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
                    .andExpect(jsonPath("$.data").isEmpty())
                    .andDo(document("point-charge-confirm",
                            requestFields(
                                    fieldWithPath("orderId").description("주문 ID")
                            ),
                            responseFields(
                                    fieldWithPath("success").description("요청 성공 여부"),
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("timestamp").description("요청 처리 시간"),
                                    fieldWithPath("data").description("응답 데이터 없음")
                            )
                    ));
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