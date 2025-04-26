package com.plus.profile.payment.application.impl;

import com.plus.profile.global.dto.CreatePaymentRequest;
import com.plus.profile.global.dto.CreatePaymentResponse;
import com.plus.profile.global.dto.PayGatewayCompany;
import com.plus.profile.payment.domain.PaymentTransaction;
import com.plus.profile.payment.domain.PaymentTransactionStatusType;
import com.plus.profile.payment.domain.repository.PaymentCancellationRepository;
import com.plus.profile.payment.domain.repository.PaymentTransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
                    PayGatewayCompany.TOSS
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
}
