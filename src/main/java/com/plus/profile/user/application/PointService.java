package com.plus.profile.user.application;

import com.plus.profile.user.presentation.dto.PointChargeRequest;
import com.plus.profile.global.dto.CreatePaymentResponse;

import java.util.UUID;

public interface PointService {
    CreatePaymentResponse chargePoint(UUID userId, PointChargeRequest request);
    void confirmPointCharge(UUID userId, UUID orderId);
}
