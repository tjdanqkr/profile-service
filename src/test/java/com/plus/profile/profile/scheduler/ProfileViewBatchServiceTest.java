package com.plus.profile.profile.scheduler;

import com.plus.profile.profile.domain.Profile;
import com.plus.profile.profile.domain.ProfileView;
import com.plus.profile.profile.domain.repository.ProfileRepository;
import com.plus.profile.profile.domain.repository.ProfileViewRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProfileViewBatchServiceTest {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ProfileViewRepository profileViewRepository;

    @Autowired
    private ProfileViewBatchService profileViewBatchService;

    private UUID profileId;

    @BeforeEach
    void setUp() {
        Profile profile = Profile.builder()
                .title("Test Profile")
                .content("Detail Content")
                .viewCount(0)
                .userId(UUID.randomUUID())
                .username("testuser")
                .build();
        profileRepository.save(profile);
        profileId = profile.getId();
    }

    @AfterEach
    void tearDown() {
        profileViewRepository.deleteAll();
        profileRepository.deleteAll();
    }

    @Nested
    @DisplayName("조회수 테이블에 저장 및 map에 추가하는 메서드")
    class AddView {

        @Test
        @DisplayName("조회수가 Map에 누적되고 ProfileView 저장된다")
        void shouldAccumulateViewsAndSaveProfileView() {
            // given
            int viewCount = 5;

            // when
            for (int i = 0; i < viewCount; i++) {
                profileViewBatchService.addView(profileId);
            }

            // then
            assertThat(profileViewBatchService.getViewMap().get(profileId)).isEqualTo(5L);

            List<ProfileView> views = profileViewRepository.findAll();
            assertThat(views).hasSize(viewCount);
        }
    }

    @Nested
    @DisplayName("map에 있는 것을 DB에 flush하는 메서드")
    class FlushViewsToDatabase {

        @Test
        @DisplayName("조회수가 Profile에 업데이트되고 Map이 비워진다")
        void shouldUpdateProfileViewCountAndClearMap() {
            // given
            int viewCount = 3;
            for (int i = 0; i < viewCount; i++) {
                profileViewBatchService.addView(profileId);
            }

            // when
            profileViewBatchService.flushViewsToDatabase();

            // then
            Profile profile = profileRepository.findById(profileId).orElseThrow();
            assertThat(profile.getViewCount()).isEqualTo(viewCount);

            assertThat(profileViewBatchService.getViewMap()).isEmpty();
        }
    }
}
