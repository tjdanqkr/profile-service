package com.plus.profile.point.application.impl;

import com.plus.profile.global.dto.ConfirmPaymentRequest;
import com.plus.profile.global.dto.ConfirmPaymentResponse;
import com.plus.profile.global.dto.CreatePaymentRequest;
import com.plus.profile.global.dto.CreatePaymentResponse;
import com.plus.profile.global.exception.BusinessException;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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
        if (!response.isSuccess()) {
            throw new BusinessException(GlobalServerException.PAYMENT_CONFIRM_FAIL);
        }
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
