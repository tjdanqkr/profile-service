package com.plus.profile.global.dto.payment;

import java.util.UUID;

public record ConfirmPaymentRequest(
        UUID userId,
        UUID orderId
) {
}
