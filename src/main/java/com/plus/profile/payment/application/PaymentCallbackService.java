package com.plus.profile.payment.application;

public interface PaymentCallbackService {
    boolean confirmTossPayment(String paymentKey, String orderId, String amount);
    boolean confirmKakaoPayment(String pgToken, String orderId);
}
