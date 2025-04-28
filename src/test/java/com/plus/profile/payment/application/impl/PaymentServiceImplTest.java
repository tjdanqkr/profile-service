package com.plus.profile.payment.application.impl;

import com.plus.profile.global.dto.*;
import com.plus.profile.global.dto.payment.*;
import com.plus.profile.payment.application.PaymentKakaoClient;
import com.plus.profile.payment.application.PaymentTossClient;
import com.plus.profile.payment.domain.PaymentConfirmStatus;
import com.plus.profile.payment.domain.PaymentTransaction;
import com.plus.profile.payment.domain.PaymentTransactionStatusType;
import com.plus.profile.payment.domain.repository.PaymentCancellationRepository;
import com.plus.profile.payment.domain.repository.PaymentTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {
    @Mock
    private PaymentTransactionRepository paymentTransactionRepository;
    @Mock
    private PaymentCancellationRepository paymentCancellationRepository;
    @InjectMocks
    private PaymentServiceImpl paymentService;
    @Mock
    private PaymentKakaoClient paymentKakaoClient;
    @Mock
    private PaymentTossClient paymentTossClient;

    private UUID orderId;

    private UUID userId;
    private PaymentTransaction transaction;
    @BeforeEach
    void setUp(){
        userId = UUID.randomUUID();
        orderId = UUID.randomUUID();
        transaction = PaymentTransaction.builder()
                .userId(UUID.randomUUID())
                .orderId(orderId)
                .orderName("테스트 주문")
                .transactionAmount(1000L)
                .transactionStatus(PaymentTransactionStatusType.PENDING)
                .pgType(PayGatewayCompany.TOSS)
                .build();
    }


    @Nested
    @DisplayName("createTransaction 메서드 테스트")
    class CreateTransactionTest {

        @Test
        @DisplayName("결제 트랜잭션 생성에 성공한다")
        void shouldCreateTransactionSuccessfully() {
            // given
            CreatePaymentRequest request = new CreatePaymentRequest(
                    UUID.randomUUID(),
                    5000L,
                    PayGatewayCompany.TOSS,
                    "test-support-key"
            );

            PaymentTransaction transaction = PaymentTransaction.builder()
                    .userId(request.userId())
                    .orderName(String.format("[포인트 충전] %s 포인트 충전합니다.", request.amount()))
                    .orderId(UUID.randomUUID())
                    .transactionAmount(request.amount())
                    .transactionStatus(PaymentTransactionStatusType.PENDING)
                    .pgType(request.pgType())
                    .build();
            when(paymentTransactionRepository.save(any(PaymentTransaction.class)))
                    .thenReturn(transaction);

            // when
            CreatePaymentResponse response = paymentService.createTransaction(request);

            // then
            assertThat(response).isNotNull();
            assertThat(response.orderId()).isNotNull();
            assertThat(response.orderName()).isEqualTo("[포인트 충전] 5000 포인트 충전합니다.");
            assertThat(response.transactionAmount()).isEqualTo(5000L);
            assertThat(response.transactionStatus()).isEqualTo(PaymentTransactionStatusType.PENDING.name());
            assertThat(response.pgType()).isEqualTo(PayGatewayCompany.TOSS.name());
            verify(paymentTransactionRepository, times(1)).save(any(PaymentTransaction.class));

        }
    }
    @Nested
    @DisplayName("Toss 결제 승인 테스트")
    class TossConfirmTest {

        @Test
        @DisplayName("Toss 결제 승인 성공")
        void confirmTossPaymentSuccess() throws Exception {
            // given
            PaymentTransaction transaction = PaymentTransaction.builder()
                    .userId(UUID.randomUUID())
                    .orderId(orderId)
                    .orderName("테스트 주문")
                    .transactionAmount(1000L)
                    .transactionStatus(PaymentTransactionStatusType.PENDING)
                    .pgType(PayGatewayCompany.TOSS)
                    .build();
            when(paymentTransactionRepository.findByOrderId(orderId)).thenReturn(Optional.of(transaction));
            when(paymentTossClient.approve(anyString(), anyString())).thenReturn(anyString());

            // when
            boolean result = paymentService.confirmTossPayment("paymentKey", orderId.toString(), "1000");

            // then
            assertThat(result).isTrue();
            assertThat(transaction.getTransactionStatus()).isEqualTo(PaymentTransactionStatusType.COMPLETED);
        }

        @Test
        @DisplayName("Toss 결제 승인 실패")
        void confirmTossPaymentFail() throws Exception {
            // given
            PaymentTransaction transaction = PaymentTransaction.builder()
                    .userId(UUID.randomUUID())
                    .orderId(orderId)
                    .orderName("테스트 주문")
                    .transactionAmount(1000L)
                    .transactionStatus(PaymentTransactionStatusType.PENDING)
                    .pgType(PayGatewayCompany.TOSS)
                    .build();
            when(paymentTransactionRepository.findByOrderId(orderId)).thenReturn(Optional.of(transaction));
            when(paymentTossClient.approve(anyString(), anyString())).thenThrow(new RuntimeException("승인 실패"));

            // when
            boolean result = paymentService.confirmTossPayment("paymentKey", orderId.toString(), "1000");

            // then
            assertThat(result).isFalse();
            assertThat(transaction.getTransactionStatus()).isEqualTo(PaymentTransactionStatusType.FAILED);
        }
    }

    @Nested
    @DisplayName("Kakao 결제 승인 테스트")
    class KakaoConfirmTest {

        @Test
        @DisplayName("Kakao 결제 승인 성공")
        void confirmKakaoPaymentSuccess() throws Exception {
            // given
            PaymentTransaction transaction = PaymentTransaction.builder()
                    .userId(UUID.randomUUID())
                    .orderId(orderId)
                    .orderName("테스트 주문")
                    .transactionAmount(1000L)
                    .transactionStatus(PaymentTransactionStatusType.PENDING)
                    .pgType(PayGatewayCompany.KAKAO)
                    .pgSupportKey("test-support-key")
                    .build();
            when(paymentTransactionRepository.findByOrderId(orderId)).thenReturn(Optional.of(transaction));
            when(paymentKakaoClient.approve(anyString(), anyString(), anyString())).thenReturn(anyString());

            // when
            boolean result = paymentService.confirmKakaoPayment("pg_token", orderId.toString());

            // then
            assertThat(result).isTrue();
            assertThat(transaction.getTransactionStatus()).isEqualTo(PaymentTransactionStatusType.COMPLETED);
        }

        @Test
        @DisplayName("Kakao 결제 승인 실패")
        void confirmKakaoPaymentFail() throws Exception {
            // given
            PaymentTransaction transaction = PaymentTransaction.builder()
                    .userId(UUID.randomUUID())
                    .orderId(orderId)
                    .orderName("테스트 주문")
                    .transactionAmount(1000L)
                    .transactionStatus(PaymentTransactionStatusType.PENDING)
                    .pgType(PayGatewayCompany.KAKAO)
                    .pgSupportKey("test-support-key")
                    .build();
            when(paymentTransactionRepository.findByOrderId(orderId)).thenReturn(Optional.of(transaction));
            when(paymentKakaoClient.approve(anyString(), anyString(), anyString())).thenThrow(new RuntimeException("승인 실패"));

            // when
            boolean result = paymentService.confirmKakaoPayment("pg_token", orderId.toString());

            // then
            assertThat(result).isFalse();
            assertThat(transaction.getTransactionStatus()).isEqualTo(PaymentTransactionStatusType.FAILED);
        }
    }
    @Nested
    @DisplayName("confirmPointCharge 메서드 테스트")
    class ConfirmPointChargeTest {

        @Test
        @DisplayName("거래가 존재하지 않으면 NOT_FOUND 반환")
        void shouldReturnNotFound() {
            // given
            when(paymentTransactionRepository.findByOrderIdAndUserId(orderId, userId))
                    .thenReturn(Optional.empty());

            // when
            ConfirmPaymentResponse response = paymentService.confirmPointCharge(new ConfirmPaymentRequest(userId, orderId));

            // then
            assertThat(response.status()).isEqualTo(ConfirmPaymentResult.NOT_FOUND);
        }

        @Test
        @DisplayName("거래가 존재하지만 상태가 COMPLETED가 아니면 PENDING 반환")
        void shouldReturnPendingIfNotCompleted() {
            // given
            PaymentTransaction pendingTransaction = PaymentTransaction.builder()
                    .orderId(orderId)
                    .userId(userId)
                    .transactionStatus(PaymentTransactionStatusType.PENDING)
                    .build();

            when(paymentTransactionRepository.findByOrderIdAndUserId(orderId, userId))
                    .thenReturn(Optional.of(pendingTransaction));

            // when
            ConfirmPaymentResponse response = paymentService.confirmPointCharge(new ConfirmPaymentRequest(userId, orderId));

            // then
            assertThat(response.status()).isEqualTo(ConfirmPaymentResult.PENDING);
        }

        @Test
        @DisplayName("결제 완료되었지만 이미 포인트 반영된 경우 ALREADY_PROCESSED 반환")
        void shouldReturnAlreadyProcessedIfConfirmed() {

            // given
            PaymentTransaction confirmedTransaction = PaymentTransaction.builder()
                    .orderId(orderId)
                    .userId(userId)
                    .transactionStatus(PaymentTransactionStatusType.COMPLETED)
                    .paymentConfirmStatus(PaymentConfirmStatus.CONFIRMED)
                    .build();

            when(paymentTransactionRepository.findByOrderIdAndUserId(orderId, userId))
                    .thenReturn(Optional.of(confirmedTransaction));

            // when
            ConfirmPaymentResponse response = paymentService.confirmPointCharge(new ConfirmPaymentRequest(userId, orderId));

            // then
            assertThat(response.status()).isEqualTo(ConfirmPaymentResult.ALREADY_PROCESSED);
        }

        @Test
        @DisplayName("정상 결제 완료 상태이면 SUCCESS 반환")
        void shouldReturnSuccess() {
            // given
            PaymentTransaction completedTransaction = PaymentTransaction.builder()
                    .orderId(orderId)
                    .userId(userId)
                    .transactionStatus(PaymentTransactionStatusType.COMPLETED)
                    .paymentConfirmStatus(PaymentConfirmStatus.PENDING)
                    .transactionAmount(5000L)
                    .build();

            when(paymentTransactionRepository.findByOrderIdAndUserId(orderId, userId))
                    .thenReturn(Optional.of(completedTransaction));

            // when
            ConfirmPaymentResponse response = paymentService.confirmPointCharge(new ConfirmPaymentRequest(userId, orderId));

            // then
            assertThat(response.status()).isEqualTo(ConfirmPaymentResult.SUCCESS);
            assertThat(response.transactionAmount()).isEqualTo(5000L);
        }
    }

}
