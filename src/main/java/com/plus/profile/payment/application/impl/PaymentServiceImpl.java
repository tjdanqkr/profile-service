package com.plus.profile.payment.application.impl;

import com.plus.profile.global.dto.CreatePaymentRequest;
import com.plus.profile.global.dto.CreatePaymentResponse;
import com.plus.profile.global.exception.BusinessException;
import com.plus.profile.payment.application.PaymentService;
import com.plus.profile.payment.domain.PaymentTransaction;
import com.plus.profile.payment.domain.PaymentTransactionStatusType;
import com.plus.profile.payment.domain.repository.PaymentCancellationRepository;
import com.plus.profile.payment.domain.repository.PaymentTransactionRepository;
import com.plus.profile.payment.exception.PaymentExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentCancellationRepository paymentCancellationRepository;

    @Override
    public CreatePaymentResponse createTransaction(CreatePaymentRequest request) {
        PaymentTransaction transaction = PaymentTransaction.builder()
                .userId(request.userId())
                .orderName(String.format("[포인트 충전] %s 포인트 충전합니다.", request.amount()))
                .orderId(UUID.randomUUID())
                .transactionAmount(request.amount())
                .transactionStatus(PaymentTransactionStatusType.PENDING)
                .pgType(request.pgType())
                .build();
        paymentTransactionRepository.save(transaction);
        return new CreatePaymentResponse(
                transaction.getOrderId(),
                transaction.getOrderName(),
                transaction.getTransactionAmount(),
                transaction.getTransactionStatus().name(),
                transaction.getPgType().name()
        );
    }


}
