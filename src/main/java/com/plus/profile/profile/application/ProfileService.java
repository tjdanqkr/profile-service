package com.plus.profile.profile.application;

import com.plus.profile.profile.presentation.dto.ProfileDetailResponse;

import java.util.UUID;

public interface ProfileService {
    ProfileDetailResponse getProfileDetail(UUID profileId);
}
