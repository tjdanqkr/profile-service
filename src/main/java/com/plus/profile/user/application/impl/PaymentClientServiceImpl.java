package com.plus.profile.user.application.impl;

import com.plus.profile.global.client.PaymentClientService;
import com.plus.profile.global.dto.CreatePaymentRequest;
import com.plus.profile.global.dto.CreatePaymentResponse;
import com.plus.profile.user.application.UserPaymentClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentClientServiceImpl implements UserPaymentClientService {
    private final PaymentClientService paymentServiceImpl;
    @Override
    public CreatePaymentResponse createTransaction(CreatePaymentRequest request) {
        return paymentServiceImpl.createTransaction(request);
    }

}
