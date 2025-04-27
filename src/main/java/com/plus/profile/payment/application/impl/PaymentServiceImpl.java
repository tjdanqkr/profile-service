package com.plus.profile.payment.application.impl;

import com.plus.profile.global.dto.*;
import com.plus.profile.global.exception.BusinessException;
import com.plus.profile.payment.application.PaymentCallbackService;
import com.plus.profile.payment.application.PaymentKakaoClient;
import com.plus.profile.payment.application.PaymentService;
import com.plus.profile.payment.application.PaymentTossClient;

import com.plus.profile.payment.domain.PaymentConfirmStatus;
import com.plus.profile.payment.domain.PaymentTransaction;
import com.plus.profile.payment.domain.PaymentTransactionStatusType;
import com.plus.profile.payment.domain.repository.PaymentCancellationRepository;
import com.plus.profile.payment.domain.repository.PaymentTransactionRepository;
import com.plus.profile.payment.exception.PaymentExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService, PaymentCallbackService {
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentCancellationRepository paymentCancellationRepository;
    private final PaymentKakaoClient paymentKakaoClient;
    private final PaymentTossClient paymentTossClient;

    @Override
    public CreatePaymentResponse createTransaction(CreatePaymentRequest request) {
        PaymentTransaction transaction = PaymentTransaction.builder()
                .userId(request.userId())
                .orderName(String.format("[포인트 충전] %s 포인트 충전합니다.", request.amount()))
                .orderId(UUID.randomUUID())
                .transactionAmount(request.amount())
                .transactionStatus(PaymentTransactionStatusType.PENDING)
                .pgType(request.pgType())
                .pgSupportKey(request.pgType() != PayGatewayCompany.TOSS ? request.supportKey() : "")
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

    @Override
    public boolean confirmTossPayment(String paymentKey, String orderId, String amount) {
        PaymentTransaction transaction = paymentTransactionRepository.findByOrderId(UUID.fromString(orderId))
                .orElseThrow(() -> new BusinessException(PaymentExceptionCode.PAYMENT_NOT_FOUND));

        try {
            String response = paymentTossClient.approve(paymentKey, orderId);
            transaction.completePayment(response);
            return true;
        }catch (Exception e) {
            log.error("[TOSS 결제 승인 실패]", e);
            transaction.failPayment();
        }
        return false;

    }


    @Override
    public boolean confirmKakaoPayment(String pgToken, String orderId) {
        PaymentTransaction transaction = paymentTransactionRepository.findByOrderId(UUID.fromString(orderId))
                .orElseThrow(() -> new BusinessException(PaymentExceptionCode.PAYMENT_NOT_FOUND));
        try {
            String response = paymentKakaoClient.approve(pgToken, orderId, transaction.getPgSupportKey());
            transaction.completePayment(response);
            return true;
        } catch (Exception e) {
            log.error("[KAKAO 결제 승인 실패]", e);
            transaction.failPayment();
        }
        return false;
    }

    @Override
    public ConfirmPaymentResponse confirmPointCharge(ConfirmPaymentRequest request) {
        Optional<PaymentTransaction> byOrderId = paymentTransactionRepository.findByOrderIdAndUserId(request.orderId(), request.userId());

        if(byOrderId.isEmpty()) return new ConfirmPaymentResponse(request.userId(), request.orderId(), ConfirmPaymentResult.NOT_FOUND, 0L);
        PaymentTransaction transaction = byOrderId.get();
        if(!transaction.getTransactionStatus().equals(PaymentTransactionStatusType.COMPLETED))
            return new ConfirmPaymentResponse(request.userId(), request.orderId(), ConfirmPaymentResult.PENDING, 0L);
        if(transaction.getPaymentConfirmStatus().equals(PaymentConfirmStatus.CONFIRMED))
            return new ConfirmPaymentResponse(request.userId(), request.orderId(), ConfirmPaymentResult.ALREADY_PROCESSED, 0L);
        transaction.confirm();
        return new ConfirmPaymentResponse(
                request.userId(),
                transaction.getOrderId(),
                ConfirmPaymentResult.SUCCESS,

                transaction.getTransactionAmount()
        );
    }
}
