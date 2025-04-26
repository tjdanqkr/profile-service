package com.plus.profile.user.application.impl;

import com.plus.profile.global.dto.CreatePaymentRequest;
import com.plus.profile.global.dto.CreatePaymentResponse;
import com.plus.profile.global.dto.PayGatewayCompany;
import com.plus.profile.payment.application.PaymentService;
import com.plus.profile.user.application.UserPaymentClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentClientServiceImplTest {

    private PaymentService paymentService;
    private UserPaymentClientService paymentClientService;

    @BeforeEach
    void setUp() {
        paymentService = mock(PaymentService.class);
        paymentClientService = new PaymentClientServiceImpl(paymentService);
    }

    @Test
    @DisplayName("createTransaction을 위임하여 성공적으로 호출한다")
    void shouldDelegateCreateTransactionSuccessfully() {
        // given
        CreatePaymentRequest request = new CreatePaymentRequest(
                UUID.randomUUID(),
                5000L,
                PayGatewayCompany.TOSS
        );

        CreatePaymentResponse expectedResponse = new CreatePaymentResponse(
                UUID.randomUUID(),
                "[포인트 충전] 5000 포인트 충전합니다.",
                5000L,
                "PENDING",
                "TOSS"
        );

        when(paymentService.createTransaction(request)).thenReturn(expectedResponse);

        // when
        CreatePaymentResponse actualResponse = paymentClientService.createTransaction(request);

        // then
        verify(paymentService, times(1)).createTransaction(request);
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }
}
