package com.plus.profile.payment.application;

public interface PaymentTossClient {
    String approve(String paymentKey, String orderId) throws Exception;
}
