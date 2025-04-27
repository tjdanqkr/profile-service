package com.plus.profile.payment.application.impl;

import com.plus.profile.payment.application.PaymentTossClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTossClientImplTest {
    PaymentTossClient paymentTossClient;
    @BeforeEach
    void setUp(){
        paymentTossClient = new PaymentTossClientImpl();
    }
    @Test
    void approve() throws Exception {
        // given
        String paymentKey = "paymentKey";
        String orderId = "orderId";

        // when
        String result = paymentTossClient.approve(paymentKey, orderId);

        // then
        assertNotNull(result);
    }
}