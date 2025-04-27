package com.plus.profile.global.dto;

import java.util.UUID;

public record ConfirmPaymentResponse(
        UUID userId,
        UUID orderId,
        boolean isSuccess,
        long transactionAmount
) {
}
