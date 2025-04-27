package com.plus.profile.user.presentation;

import com.plus.profile.global.dto.ApiResponse;

import com.plus.profile.user.application.UserService;
import com.plus.profile.user.presentation.dto.UserDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userServiceImpl;
    @GetMapping("/api/v1/users/{userId}")
    public ApiResponse<UserDetailResponse> getUserDetail(@PathVariable UUID userId) {
        return ApiResponse.success(userServiceImpl.getUserDetail(userId));
    }


}
