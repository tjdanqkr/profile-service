    package com.plus.profile.global.client;

    import com.plus.profile.global.dto.ConfirmPaymentRequest;
    import com.plus.profile.global.dto.ConfirmPaymentResponse;
    import com.plus.profile.global.dto.CreatePaymentRequest;
    import com.plus.profile.global.dto.CreatePaymentResponse;


    public interface PaymentClientService {
        CreatePaymentResponse createTransaction(CreatePaymentRequest request);
        ConfirmPaymentResponse confirmPointCharge(ConfirmPaymentRequest request);

    }
