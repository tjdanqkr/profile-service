package com.plus.profile.profile.infra.impl;

import com.plus.profile.profile.domain.Profile;
import com.plus.profile.profile.domain.repository.ProfileRepository;
import com.plus.profile.profile.domain.repository.ProfileViewRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ProfileRepositoryCustomImplTest {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ProfileRepositoryCustomImpl profileRepositoryCustomImpl;

    @Autowired
    private ProfileViewRepository profileViewRepository;

    private UUID profileId;

    @BeforeEach
    void setUp() {
        Profile profile = Profile.builder()
                .title("Test Profile")
                .content("Detail")
                .viewCount(0)
                .userId(UUID.randomUUID())
                .username("testuser")
                .build();
        profileRepository.save(profile);
        profileId = profile.getId();
    }

    @Nested
    @DisplayName("프로필 아이디로 조회")
    class FindProfileById{
        @Test
        @DisplayName("프로필이 존재할 경우")
        void testFindProfileById() {
            // given

            // when
            var result = profileRepositoryCustomImpl.findProfileById(profileId);
            // then
            assertThat(result).isPresent();
            assertThat(result.get().getTitle()).isEqualTo("Test Profile");
            assertThat(result.get().getContent()).isEqualTo("Detail");
        }
        @Test
        @DisplayName("프로필이 존재하지 않을 경우")
        void testFindProfileByIdNotFound() {
            // given
            UUID nonExistentId = UUID.randomUUID();
            // when
            var result = profileRepositoryCustomImpl.findProfileById(nonExistentId);
            // then
            assertThat(result).isNotPresent();
        }
    }
}
