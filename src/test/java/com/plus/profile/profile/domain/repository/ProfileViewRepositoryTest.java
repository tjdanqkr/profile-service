package com.plus.profile.profile.domain.repository;

import com.plus.profile.profile.domain.Profile;
import com.plus.profile.profile.domain.ProfileView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProfileViewRepositoryTest {
    @Autowired
    private ProfileViewRepository profileViewRepository;
    @Autowired
    private ProfileRepository profileRepository;
    private UUID profileId;
    @BeforeEach
    void setUp() {
        Profile profile = Profile.builder().title("Test Profile")
                .content("Detail")
                .viewCount(0)
                .userId(UUID.randomUUID())
                .username("testuser")
                .build();
        profileRepository.save(profile);
        profileId = profile.getId();
        ProfileView profileView = ProfileView.of(profile.getId());
        profileViewRepository.save(profileView);
    }
    @Nested
    @DisplayName("프로필 ID로 view 테이블 조회")
    class FindByProfileId {
        @Test
        @DisplayName("성공")
        void findByProfileId() {
            // given

            // when
            List<ProfileView> profileViews = profileViewRepository.findByProfileId(profileId);

            // then
            assertNotNull(profileViews);
            assertEquals(1, profileViews.size());
            assertEquals(profileId, profileViews.get(0).getProfile().getId());
        }
        @Test
        @DisplayName("존재하지 않는 프로필 ID로 조회")
        void findByNonExistentProfileId() {
            // given
            UUID nonExistentProfileId = UUID.randomUUID();

            // when
            List<ProfileView> profileViews = profileViewRepository.findByProfileId(nonExistentProfileId);

            // then
            assertNotNull(profileViews);
            assertTrue(profileViews.isEmpty());
        }
    }


}