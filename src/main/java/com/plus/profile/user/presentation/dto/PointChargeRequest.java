package com.plus.profile.user.presentation.dto;

import com.plus.profile.global.dto.PayGatewayCompany;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PointChargeRequest {
    private final long amount;
    private final PayGatewayCompany pgType;
}
