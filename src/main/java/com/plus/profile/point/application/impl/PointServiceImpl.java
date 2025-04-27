package com.plus.profile.point.application.impl;

import com.plus.profile.global.dto.*;
import com.plus.profile.global.exception.BusinessException;
import com.plus.profile.global.exception.GlobalPaymentException;
import com.plus.profile.global.exception.GlobalServerException;
import com.plus.profile.point.application.PointService;
import com.plus.profile.point.application.PointPaymentClientService;
import com.plus.profile.point.domain.UserPoint;
import com.plus.profile.point.domain.UserPointLog;
import com.plus.profile.point.domain.repository.UserPointLogRepository;
import com.plus.profile.point.domain.repository.UserPointRepository;
import com.plus.profile.user.exception.UserExceptionCode;
import com.plus.profile.point.presentation.dto.PointChargeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {
    private final PointPaymentClientService paymentService;
    private final UserPointRepository userPointRepository;
    private final UserPointLogRepository userPointLogRepository;
    @Override
    public CreatePaymentResponse chargePoint(UUID userId, PointChargeRequest request) {
        CreatePaymentRequest paymentRequest = new CreatePaymentRequest(userId, request.amount(), request.pgType(), request.supportKey());
        return paymentService.createTransaction(paymentRequest);
    }

    @Override
    @Transactional
    public void confirmPointCharge(UUID userId, UUID orderId) {
        ConfirmPaymentResponse response = paymentService.confirmPointCharge(new ConfirmPaymentRequest(userId, orderId));
        if (response.status().equals(ConfirmPaymentResult.NOT_FOUND))
            throw new BusinessException(GlobalPaymentException.PAYMENT_CONFIRM_NOT_FOUND);

        if (response.status().equals(ConfirmPaymentResult.PENDING))
            throw new BusinessException(GlobalPaymentException.PAYMENT_CONFIRM_PENDING);

        if (response.status().equals(ConfirmPaymentResult.ALREADY_PROCESSED))
            throw new BusinessException(GlobalPaymentException.PAYMENT_CONFIRM_ALREADY);

        UserPoint userPoint = userPointRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(UserExceptionCode.USER_NOT_FOUND));

        long before = userPoint.getPoint();
        long after = before + response.transactionAmount();
        userPoint.charge(response.transactionAmount());

        userPointLogRepository.save(
                UserPointLog.createChargeLog(
                        userId,
                        before,
                        after,
                        response.transactionAmount()
                )
        );
    }
}
