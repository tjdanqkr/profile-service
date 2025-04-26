package com.plus.profile.profile.application.impl;

import com.plus.profile.global.exception.BusinessException;
import com.plus.profile.profile.domain.Profile;
import com.plus.profile.profile.domain.repository.ProfileRepository;
import com.plus.profile.profile.exception.ProfileExceptionCode;
import com.plus.profile.profile.infra.ProfileRepositoryCustom;
import com.plus.profile.profile.presentation.dto.ProfileDetailResponse;
import com.plus.profile.profile.presentation.dto.ProfileResponse;
import com.plus.profile.profile.scheduler.ProfileViewBatchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ProfileServiceImplTest {
    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private ProfileRepositoryCustom profileRepositoryCustom;

    @Mock
    private ProfileViewBatchService profileViewBatchService;

    @InjectMocks
    private ProfileServiceImpl profileService;

    @Nested
    @DisplayName("getProfiles 메서드 테스트")
    class GetProfiles {

        @Test
        @DisplayName("정상적인 페이징 요청 시 ProfileResponse 리스트를 반환한다")
        void shouldReturnProfileListWhenPageSizeIsValid() {
            // given
            PageRequest pageable = PageRequest.of(0, 10);
            Page<ProfileResponse> mockPage = new PageImpl<>(Collections.emptyList());
            given(profileRepositoryCustom.findProfiles(pageable)).willReturn(mockPage);

            // when
            Page<ProfileResponse> result = profileService.getProfiles(pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
        }

        @Test
        @DisplayName("pageSize가 1000을 초과하면 BusinessException을 던진다")
        void shouldThrowBusinessExceptionWhenPageSizeIsTooLarge() {
            // given
            PageRequest pageable = PageRequest.of(0, 1001);

            // when & then
            assertThatThrownBy(() -> profileService.getProfiles(pageable))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ProfileExceptionCode.PAGE_SIZE_TOO_LARGE.getMessage());
        }
    }

    @Nested
    @DisplayName("getProfileDetail 메서드 테스트")
    class GetProfileDetail {

        @Test
        @DisplayName("프로필이 존재하면 정상적으로 반환하고 조회수 추가")
        void shouldReturnProfileDetailAndAddView() {
            // given
            UUID profileId = UUID.randomUUID();
            Profile profile = Profile.builder().id(profileId).title("title").content("content").build();
            ProfileDetailResponse response = new ProfileDetailResponse(profile);

            given(profileRepositoryCustom.findProfileById(profileId)).willReturn(Optional.of(response));

            // when
            ProfileDetailResponse result = profileService.getProfileDetail(profileId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getProfileId()).isEqualTo(profileId);
            assertThat(result.getTitle()).isEqualTo("title");

            then(profileViewBatchService).should(times(1)).addView(profileId);
        }

        @Test
        @DisplayName("프로필이 존재하지 않으면 BusinessException 발생")
        void shouldThrowBusinessExceptionWhenProfileNotFound() {
            // given
            UUID profileId = UUID.randomUUID();
            given(profileRepositoryCustom.findProfileById(profileId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> profileService.getProfileDetail(profileId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ProfileExceptionCode.PROFILE_NOT_FOUND.getMessage());

            then(profileViewBatchService).shouldHaveNoInteractions();
        }
    }
}