package com.plus.profile.point.presentation;

import com.plus.profile.global.dto.ApiResponse;
import com.plus.profile.global.dto.payment.CreatePaymentResponse;
import com.plus.profile.point.application.PointService;
import com.plus.profile.point.presentation.dto.PointChargeConfirmRequest;
import com.plus.profile.point.presentation.dto.PointChargeRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PointController {
    private final PointService pointService;
    @PostMapping("/api/v1/users/{userId}/points")
    public ApiResponse<CreatePaymentResponse> chargePoint(@PathVariable UUID userId,
                                                          @RequestBody @Valid PointChargeRequest request
    ) {
        CreatePaymentResponse response = pointService.chargePoint(userId, request);
        return ApiResponse.success(response);
    }
    @PostMapping("/api/v1/users/{userId}/points/confirm")
    public ApiResponse<Void> confirmPointCharge(@PathVariable UUID userId,
                                                @RequestBody PointChargeConfirmRequest request) {
        pointService.confirmPointCharge(userId, request.orderId());
        return ApiResponse.success(Void.TYPE.cast(null));
    }
}
