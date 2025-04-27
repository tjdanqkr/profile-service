package com.plus.profile.point.presentation.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PointChargeConfirmRequest(
        @NotNull(message = "Value is required.") UUID orderId) {
}
