package com.plus.profile.payment.presentation;


import com.plus.profile.global.dto.ApiResponse;
import com.plus.profile.payment.application.PaymentCallbackService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 결제 콜백을 처리하고 프론트로 리다이렉트 해야하지만 생략
 */
@RestController
@RequiredArgsConstructor
public class PaymentCallbackController {
    private final PaymentCallbackService paymentService;
    @GetMapping("/api/v1/payments/toss/callback")
    public ApiResponse<String> handlePaymentCallback(
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam String amount,
            HttpServletResponse response
    ) throws IOException {
        boolean result = paymentService.confirmTossPayment(paymentKey, orderId, amount);

//        response.sendRedirect(result ? String.format("/payments/toss/success?orderId=%s", orderId) : "/payments/toss/fail");
        return ApiResponse.success(result ? String.format("/payments/toss/success?orderId=%s", orderId) : "/payments/toss/fail");
    }
    @GetMapping("/api/v1/payments/kakao/callback")
    public ApiResponse<String>  handleCallback(
            @RequestParam String pg_token,
            @RequestParam String orderId,
            HttpServletResponse response) throws IOException {

        boolean result = paymentService.confirmKakaoPayment(pg_token, orderId);
//        response.sendRedirect(result ? String.format("/payments/kakao/success?orderId=%s", orderId)  : "/payments/kakao/fail");
        return ApiResponse.success(result ? String.format("/payments/kakao/success?orderId=%s", orderId)  : "/payments/kakao/fail");
    }
}
