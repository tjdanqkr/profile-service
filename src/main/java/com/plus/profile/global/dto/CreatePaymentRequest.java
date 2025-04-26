package com.plus.profile.global.dto;

import java.util.UUID;


public record CreatePaymentRequest
        (UUID userId, long amount, PayGatewayCompany pgType) {
}
