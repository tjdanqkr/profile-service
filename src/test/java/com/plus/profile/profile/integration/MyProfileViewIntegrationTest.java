package com.plus.profile.profile.integration;

import com.plus.profile.profile.application.ProfileService;
import com.plus.profile.profile.domain.MyProfile;
import com.plus.profile.profile.domain.repository.ProfileRepository;
import com.plus.profile.profile.scheduler.ProfileViewFlushScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MyProfileViewIntegrationTest {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ProfileViewFlushScheduler profileViewFlushScheduler;

    private UUID profileId;

    @BeforeEach
    void setUp() {
        MyProfile myProfile = MyProfile.builder()
                .title("Integration Test Profile")
                .content("Detail Content")
                .viewCount(0)
                .userId(UUID.randomUUID())
                .username("integrationTestUser")
                .build();
        profileRepository.save(myProfile);
        profileId = myProfile.getId();
    }

    @Nested
    @DisplayName("프로필 조회 통합 플로우")
    class FullIntegrationFlow {

        @Test
        @DisplayName("동시 조회 후 조회수가 정확히 증가한다")
        void shouldIncreaseViewCountAfterConcurrentViews() throws Exception {
            // given
            int threadCount = 20;
            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);

            // when
            for (int i = 0; i < threadCount; i++) {
                executorService.submit(() -> {
                    try {
                        profileService.getProfileDetail(profileId);
                    } finally {
                        latch.countDown();
                    }
                });
            }
            latch.await();

            profileViewFlushScheduler.flush();

            // then
            MyProfile myProfile = profileRepository.findById(profileId).orElseThrow();
            assertThat(myProfile.getViewCount()).isEqualTo(threadCount);
        }
    }
}
