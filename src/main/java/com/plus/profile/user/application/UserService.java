package com.plus.profile.user.application;

import com.plus.profile.user.presentation.dto.UserDetailResponse;

import java.util.UUID;

public interface UserService {
    UserDetailResponse getUserDetail(UUID userId);
}
