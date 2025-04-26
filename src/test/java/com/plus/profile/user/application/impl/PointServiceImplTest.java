package com.plus.profile.user.application.impl;

import com.plus.profile.global.dto.CreatePaymentRequest;
import com.plus.profile.global.dto.CreatePaymentResponse;
import com.plus.profile.global.dto.PayGatewayCompany;
import com.plus.profile.user.application.UserPaymentClientService;
import com.plus.profile.user.domain.repository.UserPointRepository;
import com.plus.profile.user.presentation.dto.PointChargeRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointServiceImplTest {
    @Mock
    private UserPaymentClientService userPaymentClientService;
    @Mock
    private UserPointRepository userPointRepository;
    @InjectMocks
    private PointServiceImpl pointService;


    @Test
    @DisplayName("포인트 충전 요청을 정상적으로 처리한다")
    void shouldChargePointSuccessfully() {
        // given
        UUID userId = UUID.randomUUID();
        PointChargeRequest request = new PointChargeRequest(5000L, PayGatewayCompany.TOSS);

        CreatePaymentResponse expectedResponse = new CreatePaymentResponse(
                UUID.randomUUID(),
                "[포인트 충전] 5000 포인트 충전합니다.",
                5000L,
                "PENDING",
                "TOSS"
        );

        when(userPaymentClientService.createTransaction(any(CreatePaymentRequest.class)))
                .thenReturn(expectedResponse);

        // when
        CreatePaymentResponse actualResponse = pointService.chargePoint(userId, request);

        // then
        verify(userPaymentClientService, times(1))
                .createTransaction(any(CreatePaymentRequest.class));
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }
}
