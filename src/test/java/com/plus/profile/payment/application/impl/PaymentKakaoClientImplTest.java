package com.plus.profile.payment.application.impl;

import com.plus.profile.payment.application.PaymentKakaoClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentKakaoClientImplTest {
    PaymentKakaoClient paymentKakaoClient;
    @BeforeEach
    void setUp(){
        paymentKakaoClient = new PaymentKakaoClientImpl();
    }

    @Test
    void approve() throws Exception {
        // given
        String paymentKey = "paymentKey";
        String orderId = "orderId";
        String supportKey = "supportKey";

        // when
        String result = paymentKakaoClient.approve(paymentKey, orderId, supportKey);

        // then
        assertNotNull(result);
    }
}