package com.plus.profile.payment.presentation;

import com.plus.profile.payment.application.PaymentCallbackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(PaymentCallbackController.class)
@AutoConfigureRestDocs
class PaymentCallbackControllerTest {

    private MockMvc mockMvc;

    @MockitoBean
    private PaymentCallbackService paymentCallbackService;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Nested
    @DisplayName("토스 결제 콜백")
    class TossCallback {

        @Test
        @DisplayName("성공하면 성공 페이지로 리다이렉트한다")
        void tossPaymentSuccess() throws Exception {
            Mockito.when(paymentCallbackService.confirmTossPayment(any(), any(), any()))
                    .thenReturn(true);

            mockMvc.perform(get("/api/v1/payments/toss/callback")
                            .param("paymentKey", "samplePaymentKey")
                            .param("orderId", "sampleOrderId")
                            .param("amount", "10000"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").value("/payments/toss/success?orderId=sampleOrderId"))
                    .andDo(document("payments-toss-callback-success",
                            queryParameters(
                                    parameterWithName("paymentKey").description("토스 결제 고유 키"),
                                    parameterWithName("orderId").description("주문 고유 ID"),
                                    parameterWithName("amount").description("결제 금액")
                            ),
                            responseFields(
                                    fieldWithPath("success").description("요청 성공 여부"),
                                    fieldWithPath("data").description("리다이렉트할 성공 페이지 URL"),
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("timestamp").description("요청 처리 시간")
                            )
                    ));

        }

        @Test
        @DisplayName("실패하면 실패 페이지로 리다이렉트한다")
        void tossPaymentFail() throws Exception {
            Mockito.when(paymentCallbackService.confirmTossPayment(any(), any(), any()))
                    .thenReturn(false);

            mockMvc.perform(get("/api/v1/payments/toss/callback")
                            .param("paymentKey", "samplePaymentKey")
                            .param("orderId", "sampleOrderId")
                            .param("amount", "10000"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").value("/payments/toss/fail"))
                    .andDo(document("payments-toss-callback-fail"));
        }
    }

    @Nested
    @DisplayName("카카오 결제 콜백")
    class KakaoCallback {

        @Test
        @DisplayName("성공하면 성공 페이지로 리다이렉트한다")
        void kakaoPaymentSuccess() throws Exception {
            Mockito.when(paymentCallbackService.confirmKakaoPayment(any(), any()))
                    .thenReturn(true);

            mockMvc.perform(get("/api/v1/payments/kakao/callback")
                            .param("pg_token", "samplePgToken")
                            .param("orderId", "sampleOrderId"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").value("/payments/kakao/success?orderId=sampleOrderId"))
                    .andExpect(jsonPath("$.statusCode").value(200))
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andDo(document("payments-kakao-callback-success",
                            queryParameters(
                                    parameterWithName("pg_token").description("카카오 결제 고유 키"),
                                    parameterWithName("orderId").description("주문 고유 ID")
                            ),
                            responseFields(
                                    fieldWithPath("success").description("요청 성공 여부"),
                                    fieldWithPath("data").description("리다이렉트할 성공 페이지 URL"),
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("timestamp").description("요청 처리 시간")
                            )
                    ));
        }

        @Test
        @DisplayName("실패하면 실패 페이지로 리다이렉트한다")
        void kakaoPaymentFail() throws Exception {
            Mockito.when(paymentCallbackService.confirmKakaoPayment(any(), any()))
                    .thenReturn(false);

            mockMvc.perform(get("/api/v1/payments/kakao/callback")
                            .param("pg_token", "samplePgToken")
                            .param("orderId", "sampleOrderId"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").value("/payments/kakao/fail"))
                    .andExpect(jsonPath("$.statusCode").value(200))
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andDo(document("payments-kakao-callback-fail"));
        }
    }
}
