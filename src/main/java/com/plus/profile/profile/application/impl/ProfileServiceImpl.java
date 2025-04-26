package com.plus.profile.profile.application.impl;

import com.plus.profile.global.exception.BusinessException;
import com.plus.profile.profile.application.ProfileService;
import com.plus.profile.profile.domain.repository.ProfileRepository;
import com.plus.profile.profile.exception.ProfileExceptionCode;
import com.plus.profile.profile.infra.ProfileRepositoryCustom;
import com.plus.profile.profile.presentation.dto.ProfileDetailResponse;
import com.plus.profile.profile.presentation.dto.ProfileResponse;
import com.plus.profile.profile.scheduler.ProfileViewBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository profileRepository;
    private final ProfileRepositoryCustom profileRepositoryCustom;
    private final ProfileViewBatchService profileViewBatchService;

    @Override
    @Transactional
    public ProfileDetailResponse getProfileDetail(UUID profileId) {
        ProfileDetailResponse response = profileRepositoryCustom.findProfileById(profileId)
                .orElseThrow(() -> new BusinessException(ProfileExceptionCode.PROFILE_NOT_FOUND));
        profileViewBatchService.addView(profileId);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProfileResponse> getProfiles(Pageable pageable) {
        if (pageable.getPageSize() > 1000) {
            throw new BusinessException(ProfileExceptionCode.PAGE_SIZE_TOO_LARGE);
        }
        return profileRepositoryCustom.findProfiles(pageable);
    }
}
