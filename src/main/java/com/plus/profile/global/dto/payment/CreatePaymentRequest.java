package com.plus.profile.global.dto.payment;

import com.plus.profile.global.dto.PayGatewayCompany;

import java.util.UUID;


public record CreatePaymentRequest
        (UUID userId, long amount, PayGatewayCompany pgType, String supportKey) {
}
