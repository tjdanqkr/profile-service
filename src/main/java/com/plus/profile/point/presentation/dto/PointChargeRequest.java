package com.plus.profile.point.presentation.dto;

import com.plus.profile.global.dto.PayGatewayCompany;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PointChargeRequest(
        @NotNull(message = "Value is required.")
        @Min(value = 100, message = "Minimum value is 100.")
        long amount,
        @NotNull(message = "Value is required.")
        PayGatewayCompany pgType,
        String supportKey)
{
}