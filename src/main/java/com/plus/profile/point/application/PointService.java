package com.plus.profile.point.application;

import com.plus.profile.point.presentation.dto.PointChargeRequest;
import com.plus.profile.global.dto.payment.CreatePaymentResponse;

import java.util.UUID;

public interface PointService {
    CreatePaymentResponse chargePoint(UUID userId, PointChargeRequest request);
    void confirmPointCharge(UUID userId, UUID orderId);
}
