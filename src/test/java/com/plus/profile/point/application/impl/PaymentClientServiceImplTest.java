package com.plus.profile.point.application.impl;

import com.plus.profile.global.dto.*;
import com.plus.profile.global.dto.payment.*;
import com.plus.profile.payment.application.PaymentService;
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
    private com.plus.profile.point.application.PointPaymentClientService paymentClientService;

    @BeforeEach
    void setUp() {
        paymentService = mock(PaymentService.class);
        paymentClientService = new PointPaymentClientServiceImpl(paymentService);
    }

    @Test
    @DisplayName("createTransaction을 위임하여 성공적으로 호출한다")
    void shouldDelegateCreateTransactionSuccessfully() {
        // given
        CreatePaymentRequest request = new CreatePaymentRequest(
                UUID.randomUUID(),
                5000L,
                PayGatewayCompany.TOSS,
                "test-support-key"
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

    @Test
    @DisplayName("결제 확인 성공")
    void confirmPointChargeSuccess() {
        // given
        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        ConfirmPaymentRequest request = new ConfirmPaymentRequest(userId, orderId);
        ConfirmPaymentResponse expectedResponse = new ConfirmPaymentResponse(userId, orderId, ConfirmPaymentResult.SUCCESS, 5000L);

        when(paymentService.confirmPointCharge(request)).thenReturn(expectedResponse);

        // when
        ConfirmPaymentResponse response = paymentClientService.confirmPointCharge(request);

        // then
        assertThat(response).isEqualTo(expectedResponse);
        verify(paymentService, times(1)).confirmPointCharge(request);
    }
}
