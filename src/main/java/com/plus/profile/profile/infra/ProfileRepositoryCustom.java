package com.plus.profile.profile.infra;

import com.plus.profile.profile.presentation.dto.ProfileDetailResponse;

import java.util.Optional;
import java.util.UUID;

public interface ProfileRepositoryCustom {
    Optional<ProfileDetailResponse> findProfileById(UUID id);
}
