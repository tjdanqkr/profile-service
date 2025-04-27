package com.plus.profile.payment.presentation;

import com.plus.profile.payment.application.PaymentCallbackService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentCallbackController.class)
class PaymentCallbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentCallbackService paymentCallbackService;

    @Nested
    @DisplayName("토스 결제 콜백")
    class TossCallback {

        @Test
        @DisplayName("성공하면 성공 페이지로 리다이렉트한다")
        void tossPaymentSuccess() throws Exception {
            // given
            Mockito.when(paymentCallbackService.confirmTossPayment(any(), any(), any()))
                    .thenReturn(true);

            // when & then
            mockMvc.perform(get("/api/v1/payments/toss/callback")
                            .param("paymentKey", "samplePaymentKey")
                            .param("orderId", "sampleOrderId")
                            .param("amount", "10000"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/payments/toss/success?orderId=sampleOrderId"));
        }

        @Test
        @DisplayName("실패하면 실패 페이지로 리다이렉트한다")
        void tossPaymentFail() throws Exception {
            // given
            Mockito.when(paymentCallbackService.confirmTossPayment(any(), any(), any()))
                    .thenReturn(false);

            // when & then
            mockMvc.perform(get("/api/v1/payments/toss/callback")
                            .param("paymentKey", "samplePaymentKey")
                            .param("orderId", "sampleOrderId")
                            .param("amount", "10000"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/payments/toss/fail"));
        }
    }

    @Nested
    @DisplayName("카카오 결제 콜백")
    class KakaoCallback {

        @Test
        @DisplayName("성공하면 성공 페이지로 리다이렉트한다")
        void kakaoPaymentSuccess() throws Exception {
            // given
            Mockito.when(paymentCallbackService.confirmKakaoPayment(any(), any()))
                    .thenReturn(true);

            // when & then
            mockMvc.perform(get("/api/v1/payments/kakao/callback")
                            .param("pg_token", "samplePgToken")
                            .param("orderId", "sampleOrderId"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/payments/kakao/success?orderId=sampleOrderId"));
        }

        @Test
        @DisplayName("실패하면 실패 페이지로 리다이렉트한다")
        void kakaoPaymentFail() throws Exception {
            // given
            Mockito.when(paymentCallbackService.confirmKakaoPayment(any(), any()))
                    .thenReturn(false);

            // when & then
            mockMvc.perform(get("/api/v1/payments/kakao/callback")
                            .param("pg_token", "samplePgToken")
                            .param("orderId", "sampleOrderId"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/payments/kakao/fail"));
        }
    }
}
