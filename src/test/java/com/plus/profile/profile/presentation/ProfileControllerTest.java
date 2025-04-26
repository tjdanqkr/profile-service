package com.plus.profile.profile.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plus.profile.global.error.BusinessException;
import com.plus.profile.profile.application.ProfileService;
import com.plus.profile.profile.domain.Profile;
import com.plus.profile.profile.exception.ProfileExceptionCode;
import com.plus.profile.profile.presentation.dto.ProfileDetailResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

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
