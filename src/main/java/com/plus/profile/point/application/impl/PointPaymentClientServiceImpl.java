package com.plus.profile.point.application.impl;

import com.plus.profile.global.client.PaymentClientService;
import com.plus.profile.global.dto.ConfirmPaymentRequest;
import com.plus.profile.global.dto.ConfirmPaymentResponse;
import com.plus.profile.global.dto.CreatePaymentRequest;
import com.plus.profile.global.dto.CreatePaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointPaymentClientServiceImpl implements com.plus.profile.point.application.PointPaymentClientService {
    private final PaymentClientService paymentServiceImpl;
    @Override
    public CreatePaymentResponse createTransaction(CreatePaymentRequest request) {
        return paymentServiceImpl.createTransaction(request);
    }

    @Override
    public ConfirmPaymentResponse confirmPointCharge(ConfirmPaymentRequest request) {
        return paymentServiceImpl.confirmPointCharge(request);
    }
}
