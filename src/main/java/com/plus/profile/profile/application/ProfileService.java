package com.plus.profile.profile.application;

import com.plus.profile.profile.presentation.dto.ProfileDetailResponse;
import com.plus.profile.profile.presentation.dto.ProfileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProfileService {
    ProfileDetailResponse getProfileDetail(UUID profileId);
    Page<ProfileResponse> getProfiles(Pageable pageable);
}
