package com.plus.profile.user.infra;

import com.plus.profile.user.presentation.dto.UserDetailResponse;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryCustom {
    Optional<UserDetailResponse> findUserDetailById(UUID userId);

}
