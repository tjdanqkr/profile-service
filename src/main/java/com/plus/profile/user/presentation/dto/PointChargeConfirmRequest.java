package com.plus.profile.user.presentation.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PointChargeConfirmRequest(
        @NotNull(message = "Value is required.") UUID orderId) {
}
