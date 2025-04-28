    package com.plus.profile.global.client;

    import com.plus.profile.global.dto.payment.ConfirmPaymentRequest;
    import com.plus.profile.global.dto.payment.ConfirmPaymentResponse;
    import com.plus.profile.global.dto.payment.CreatePaymentRequest;
    import com.plus.profile.global.dto.payment.CreatePaymentResponse;


    public interface PaymentClientService {
        CreatePaymentResponse createTransaction(CreatePaymentRequest request);
        ConfirmPaymentResponse confirmPointCharge(ConfirmPaymentRequest request);

    }
