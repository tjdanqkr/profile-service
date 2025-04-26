package com.plus.profile.profile.infra;

import com.plus.profile.profile.presentation.dto.ProfileDetailResponse;
import com.plus.profile.profile.presentation.dto.ProfileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ProfileRepositoryCustom {
    Optional<ProfileDetailResponse> findProfileById(UUID id);
    Page<ProfileResponse> findProfiles(Pageable pageable);

}
