package com.plus.profile.global.dto;

import java.util.UUID;

public record ConfirmPaymentRequest(
        UUID userId,
        UUID orderId
) {
}
