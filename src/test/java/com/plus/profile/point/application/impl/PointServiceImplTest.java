package com.plus.profile.point.application.impl;

import com.plus.profile.global.dto.*;
import com.plus.profile.global.dto.payment.*;
import com.plus.profile.global.exception.BusinessException;
import com.plus.profile.global.exception.GlobalPaymentException;
import com.plus.profile.point.application.PointPaymentClientService;
import com.plus.profile.point.domain.UserPoint;
import com.plus.profile.point.domain.UserPointLog;
import com.plus.profile.point.domain.repository.UserPointLogRepository;
import com.plus.profile.point.domain.repository.UserPointRepository;
import com.plus.profile.user.exception.UserExceptionCode;
import com.plus.profile.point.presentation.dto.PointChargeRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointServiceImplTest {
    @Mock
    private PointPaymentClientService pointPaymentClientService;
    @Mock
    private UserPointRepository userPointRepository;
    @Mock
    private UserPointLogRepository userPointLogRepository;
    @InjectMocks
    private PointServiceImpl pointService;


    @Test
    @DisplayName("포인트 충전 요청을 정상적으로 처리한다")
    void shouldChargePointSuccessfully() {
        // given
        UUID userId = UUID.randomUUID();
        PointChargeRequest request = new PointChargeRequest(5000L, PayGatewayCompany.TOSS, "test-support-key");

        CreatePaymentResponse expectedResponse = new CreatePaymentResponse(
                UUID.randomUUID(),
                "[포인트 충전] 5000 포인트 충전합니다.",
                5000L,
                "PENDING",
                "TOSS"
        );

        when(pointPaymentClientService.createTransaction(any(CreatePaymentRequest.class)))
                .thenReturn(expectedResponse);

        // when
        CreatePaymentResponse actualResponse = pointService.chargePoint(userId, request);

        // then
        verify(pointPaymentClientService, times(1))
                .createTransaction(any(CreatePaymentRequest.class));
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }
    @Nested
    @DisplayName("confirmPointCharge 메서드 테스트")
    class ConfirmPointChargeTest {

        @Test
        @DisplayName("포인트 충전 확정 성공")
        void confirmPointChargeSuccess() {
            // given
            UUID userId = UUID.randomUUID();
            UUID orderId = UUID.randomUUID();
            long chargeAmount = 5000L;

            UserPoint userPoint = UserPoint.builder()
                    .point(10000L)
                    .build();

            when(pointPaymentClientService.confirmPointCharge(any(ConfirmPaymentRequest.class)))
                    .thenReturn(new ConfirmPaymentResponse(userId, orderId, ConfirmPaymentResult.SUCCESS, chargeAmount));

            when(userPointRepository.findByUserId(userId))
                    .thenReturn(Optional.of(userPoint));

            // when
            pointService.confirmPointCharge(userId, orderId);

            // then
            assertThat(userPoint.getPoint()).isEqualTo(15000L); // 포인트가 증가했는지 검증
            verify(userPointLogRepository, times(1)).save(any(UserPointLog.class)); // 로그 저장 확인
        }

        @Test
        @DisplayName("포인트 충전 확정 실패 - 결제 미확정")
        void confirmPointChargeFailWhenPaymentNotConfirmed() {
            // given
            UUID userId = UUID.randomUUID();
            UUID orderId = UUID.randomUUID();

            when(pointPaymentClientService.confirmPointCharge(any(ConfirmPaymentRequest.class)))
                    .thenReturn(new ConfirmPaymentResponse(userId, orderId, ConfirmPaymentResult.PENDING, 0L));

            // when & then
            assertThatThrownBy(() -> pointService.confirmPointCharge(userId, orderId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(GlobalPaymentException.PAYMENT_CONFIRM_PENDING.getMessage());

            verify(userPointRepository, never()).findByUserId(any());
            verify(userPointLogRepository, never()).save(any());
        }
        @Test
        @DisplayName("포인트 충전 확정 실패 - 결제 못 찾음")
        void confirmPointChargeFailWhenPaymentNotFound() {
            // given
            UUID userId = UUID.randomUUID();
            UUID orderId = UUID.randomUUID();

            when(pointPaymentClientService.confirmPointCharge(any(ConfirmPaymentRequest.class)))
                    .thenReturn(new ConfirmPaymentResponse(userId, orderId, ConfirmPaymentResult.NOT_FOUND, 0L));

            // when & then
            assertThatThrownBy(() -> pointService.confirmPointCharge(userId, orderId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(GlobalPaymentException.PAYMENT_CONFIRM_NOT_FOUND.getMessage());

            verify(userPointRepository, never()).findByUserId(any());
            verify(userPointLogRepository, never()).save(any());
        }
        @Test
        @DisplayName("포인트 충전 확정 실패 - 이미 처리됨")
        void confirmPointChargeFailWhenPaymentAlready() {
            // given
            UUID userId = UUID.randomUUID();
            UUID orderId = UUID.randomUUID();

            when(pointPaymentClientService.confirmPointCharge(any(ConfirmPaymentRequest.class)))
                    .thenReturn(new ConfirmPaymentResponse(userId, orderId, ConfirmPaymentResult.ALREADY_PROCESSED, 0L));

            // when & then
            assertThatThrownBy(() -> pointService.confirmPointCharge(userId, orderId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(GlobalPaymentException.PAYMENT_CONFIRM_ALREADY.getMessage());

            verify(userPointRepository, never()).findByUserId(any());
            verify(userPointLogRepository, never()).save(any());
        }

        @Test
        @DisplayName("포인트 충전 확정 실패 - 유저 정보 없음")
        void confirmPointChargeFailWhenUserNotFound() {
            // given
            UUID userId = UUID.randomUUID();
            UUID orderId = UUID.randomUUID();

            when(pointPaymentClientService.confirmPointCharge(any(ConfirmPaymentRequest.class)))
                    .thenReturn(new ConfirmPaymentResponse(userId, orderId, ConfirmPaymentResult.SUCCESS, 5000L));

            when(userPointRepository.findByUserId(userId))
                    .thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> pointService.confirmPointCharge(userId, orderId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(UserExceptionCode.USER_NOT_FOUND.getMessage());

            verify(userPointLogRepository, never()).save(any());
        }
    }
}
