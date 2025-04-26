package com.plus.profile.profile.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plus.profile.global.exception.BusinessException;
import com.plus.profile.profile.application.ProfileService;
import com.plus.profile.profile.domain.Profile;
import com.plus.profile.profile.exception.ProfileExceptionCode;
import com.plus.profile.profile.presentation.dto.ProfileDetailResponse;
import com.plus.profile.profile.presentation.dto.ProfileResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfileController.class)
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProfileService profileService;

    @Autowired
    private ObjectMapper objectMapper;
    @Nested
    @DisplayName("GET /api/v1/profiles")
    class GetProfiles {

        @Test
        @DisplayName("프로필 목록을 정상적으로 반환한다")
        void shouldReturnProfilesSuccessfully() throws Exception {
            List<Profile> profiles = new ArrayList<>();
            for (int i = 1; i < 3; i++) {
                Profile build = Profile.builder()
                        .id(UUID.randomUUID())
                        .username("user" + i)
                        .title("Title" + i)
                        .content("Content" + i)
                        .viewCount(10L * i)
                        .userId(UUID.randomUUID())
                        .build();
                profiles.add(build);
            }

            // given
            List<ProfileResponse> profileList = profiles.stream().map(ProfileResponse::new).toList();
            Page<ProfileResponse> mockPage = new PageImpl<>(profileList, PageRequest.of(0, 10), 2);

            given(profileService.getProfiles(any())).willReturn(mockPage);

            // when & then
            mockMvc.perform(get("/api/v1/profiles")
                            .param("page", "0")
                            .param("size", "10")
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content.length()").value(2))
                    .andExpect(jsonPath("$.data.content[0].username").value("user1"))
                    .andExpect(jsonPath("$.data.content[1].username").value("user2"));
        }
        @Test
        @DisplayName("프로필 목록을 정상적으로 반환한다 - 페이지 정보가 없는 경우")
        void shouldReturnProfilesSuccessfullyWithoutPageInfo() throws Exception {
            List<Profile> profiles = new ArrayList<>();
            for (int i = 1; i < 3; i++) {
                Profile build = Profile.builder()
                        .id(UUID.randomUUID())
                        .username("user" + i)
                        .title("Title" + i)
                        .content("Content" + i)
                        .viewCount(10L * i)
                        .userId(UUID.randomUUID())
                        .build();
                profiles.add(build);
            }

            // given
            List<ProfileResponse> profileList = profiles.stream().map(ProfileResponse::new).toList();
            Page<ProfileResponse> mockPage = new PageImpl<>(profileList, PageRequest.of(0, 10), 2);

            given(profileService.getProfiles(any())).willReturn(mockPage);

            // when & then
            mockMvc.perform(get("/api/v1/profiles"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content.length()").value(2))
                    .andExpect(jsonPath("$.data.content[0].username").value("user1"))
                    .andExpect(jsonPath("$.data.content[1].username").value("user2"));
        }
        @Test
        @DisplayName("페이지 사이즈가 1000초과하면 400을 반환한다")
        void shouldReturnBadRequestWhenPageSizeExceedsLimit() throws Exception {
            // given
            int pageSize = 1001;
            given(profileService.getProfiles(any(Pageable.class)))
                    .willThrow(new BusinessException(ProfileExceptionCode.PAGE_SIZE_TOO_LARGE));
            // when & then
            mockMvc.perform(get("/api/v1/profiles")
                            .param("page", "0")
                            .param("size", String.valueOf(pageSize))
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Page size too large"))
                    .andExpect(jsonPath("$.statusCode").value(400));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/profiles/{profileId}")
    class GetProfileDetail {

        @Test
        @DisplayName("프로필 상세 정보를 정상적으로 반환한다")
        void shouldReturnProfileDetail() throws Exception {
            // given
            UUID profileId = UUID.randomUUID();
            Profile profile = Profile.builder().id(profileId).title("title").content("content").build();
            ProfileDetailResponse response = new ProfileDetailResponse(profile);

            given(profileService.getProfileDetail(profileId)).willReturn(response);

            // when & then
            mockMvc.perform(get("/api/v1/profiles/{profileId}", profileId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.statusCode").value(200))
                    .andExpect(jsonPath("$.data.profileId").value(profileId.toString()))
                    .andExpect(jsonPath("$.data.title").value("title"))
                    .andExpect(jsonPath("$.data.content").value("content"));
        }
        @Test
        @DisplayName("존재하지 않는 프로필 ID로 조회 시 404 에러를 반환한다")
        void shouldReturnNotFound() throws Exception {
            // given
            UUID profileId = UUID.randomUUID();
            given(profileService.getProfileDetail(profileId))
                    .willThrow(new BusinessException(ProfileExceptionCode.PROFILE_NOT_FOUND));

            // when & then
            mockMvc.perform(get("/api/v1/profiles/{profileId}", profileId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Profile not found"))
                    .andExpect(jsonPath("$.statusCode").value(404));
        }
    }
}
