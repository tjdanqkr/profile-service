package com.plus.profile.profile.presentation;

import com.plus.profile.global.dto.ApiResponse;
import com.plus.profile.profile.application.ProfileService;
import com.plus.profile.profile.presentation.dto.ProfileDetailResponse;
import com.plus.profile.profile.presentation.dto.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;
    @GetMapping("/api/v1/profiles/{profileId}")
    public ApiResponse<ProfileDetailResponse> getProfileDetail(@PathVariable UUID profileId) {
        return ApiResponse.success(profileService.getProfileDetail(profileId));
    }
    @GetMapping("/api/v1/profiles")
    public ApiResponse<Page<ProfileResponse>> getProfiles(@PageableDefault(size = 10, page = 0, sort = "createAt") Pageable pageable) {
        return ApiResponse.success(profileService.getProfiles(pageable));
    }
}
