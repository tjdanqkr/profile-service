package com.plus.profile.user.application.impl;

import com.plus.profile.global.dto.CreatePaymentRequest;
import com.plus.profile.global.dto.CreatePaymentResponse;
import com.plus.profile.user.application.PointService;
import com.plus.profile.user.application.UserPaymentClientService;
import com.plus.profile.user.domain.repository.UserPointRepository;
import com.plus.profile.user.presentation.dto.PointChargeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {
    private final UserPaymentClientService paymentService;
    private final UserPointRepository userPointRepository;
    @Override
    public CreatePaymentResponse chargePoint(UUID userId, PointChargeRequest request) {
        CreatePaymentRequest paymentRequest = new CreatePaymentRequest(userId, request.amount(), request.pgType(), request.supportKey());
        return paymentService.createTransaction(paymentRequest);
    }
}
