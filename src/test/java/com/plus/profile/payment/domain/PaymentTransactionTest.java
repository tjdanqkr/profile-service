package com.plus.profile.payment.domain;

import com.plus.profile.global.dto.PayGatewayCompany;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
class PaymentTransactionTest {

    @Test
    void completePayment() {
        PaymentTransaction build = PaymentTransaction.builder()
                .orderId(UUID.randomUUID())
                .transactionAmount(1000L)
                .pgType(PayGatewayCompany.KAKAO)
                .build();
        build.completePayment("test");
        assertEquals(PaymentTransactionStatusType.COMPLETED, build.getTransactionStatus());
        assertEquals("test", build.getPaymentResponse());
        assertEquals(PayGatewayCompany.KAKAO, build.getPgType());
    }

    @Test
    void failPayment() {
        PaymentTransaction build = PaymentTransaction.builder()
                .orderId(UUID.randomUUID())
                .transactionAmount(1000L)
                .pgType(PayGatewayCompany.KAKAO)
                .build();
        build.failPayment();
        assertEquals(PaymentTransactionStatusType.FAILED, build.getTransactionStatus());

    }
}