package com.plus.profile.user.presentation;

import com.plus.profile.global.dto.ApiResponse;
import com.plus.profile.global.dto.CreatePaymentResponse;
import com.plus.profile.user.application.UserService;
import com.plus.profile.user.presentation.dto.PointChargeRequest;
import com.plus.profile.user.presentation.dto.UserDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping("/api/v1/users/{userId}")
    public ApiResponse<UserDetailResponse> getUserDetail(@PathVariable UUID userId) {
        return ApiResponse.success(userService.getUserDetail(userId));
    }
    @PostMapping("/api/v1/users/{userId}/points")
    public ApiResponse<CreatePaymentResponse> chargePoint(@PathVariable UUID userId, @RequestBody PointChargeRequest request) {
        return ApiResponse.success(userService.chargePoint(userId));
    }
}
