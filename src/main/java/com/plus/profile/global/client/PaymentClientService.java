package com.plus.profile.global.client;

import com.plus.profile.global.dto.CreatePaymentRequest;
import com.plus.profile.global.dto.CreatePaymentResponse;


public interface PaymentClientService {
    CreatePaymentResponse createTransaction(CreatePaymentRequest request);

}
