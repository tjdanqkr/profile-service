package com.plus.profile.payment.application;

public interface PaymentKakaoClient {
    String approve(String pgToken, String orderId, String supportKey) throws Exception;
}
