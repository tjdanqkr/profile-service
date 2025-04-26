package com.plus.profile.profile.scheduler;

import com.plus.profile.profile.domain.Profile;
import com.plus.profile.profile.domain.repository.ProfileRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProfileViewFlushSchedulerTest {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ProfileViewBatchService profileViewBatchService;

    @Autowired
    private ProfileViewFlushScheduler profileViewFlushScheduler;

    @Autowired
    private EntityManager em;

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

    @Nested
    @DisplayName("데이터 베이스 반영 스케쥴러 메소드")
    class FlushScheduler {

        @Test
        @DisplayName("Profile의 조회수가 갱신되고 viewMap이 비워진다")
        void shouldFlushViewCountsAndClearMap() {
            // given
            int viewCount = 5;
            for (int i = 0; i < viewCount; i++) {
                profileViewBatchService.addView(profileId);
            }

            // when
            profileViewFlushScheduler.flush();
            em.flush();
            em.clear();

            // then
            Profile profile = profileRepository.findById(profileId).orElseThrow();
            assertThat(profile.getViewCount()).isEqualTo(viewCount);

            assertThat(profileViewBatchService.getViewMap()).isEmpty();
        }
    }
}
