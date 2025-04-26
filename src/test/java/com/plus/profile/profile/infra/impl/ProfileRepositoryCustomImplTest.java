package com.plus.profile.profile.infra.impl;

import com.plus.profile.profile.domain.Profile;
import com.plus.profile.profile.domain.repository.ProfileRepository;
import com.plus.profile.profile.domain.repository.ProfileViewRepository;
import com.plus.profile.profile.presentation.dto.ProfileResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ProfileRepositoryCustomImplTest {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ProfileRepositoryCustomImpl profileRepositoryCustomImpl;

    private List<Profile> profiles;
    private final String[] names = {
            "김민준", "이서준", "박예린", "최지우", "정하준",
            "강서연", "윤도윤", "조은우", "한나윤", "오지민",
            "서지후", "홍시우", "안도현", "유지아", "백서윤",
            "심준호", "문하은", "권민재", "남지안", "임지호",
            "곽하람", "배주원", "천이준", "양유나", "노도연",
            "구채원", "민서진", "하도훈", "방예진", "서다온"
    };

    @BeforeEach
    void setUp() {
        profiles = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            String name = names[i];
            Profile profile = Profile.builder()
                    .username(name)
                    .title("title" + name)
                    .content("content" + name)
                    .viewCount(i)
                    .userId(UUID.randomUUID())
                    .build();
            profiles.add(profile);
        }
        profileRepository.saveAll(profiles);
    }

    @Nested
    @DisplayName("findProfiles 메서드 테스트")
    class FindProfiles {

        @Test
        @DisplayName("기본 페이징 조회가 정상적으로 동작한다")
        void shouldReturnProfilesWithPaging() {
            // given
            PageRequest pageable = PageRequest.of(0, 5);

            // when
            Page<ProfileResponse> page = profileRepositoryCustomImpl.findProfiles(pageable);

            // then
            assertThat(page.getContent()).hasSize(5);
            assertThat(page.getTotalElements()).isEqualTo(30);
            assertThat(page.getNumber()).isEqualTo(0);
        }

        @Test
        @DisplayName("username 기준 오름차순 정렬이 정상적으로 적용된다")
        void shouldSortProfilesByUsernameAsc() {
            // given
            PageRequest pageable = PageRequest.of(0, 5, Sort.by("username").ascending());

            // when
            Page<ProfileResponse> page = profileRepositoryCustomImpl.findProfiles(pageable);
            List<ProfileResponse> responses = page.getContent();

            // then
            List<String> list = Arrays.stream(names).sorted().toList();
            assertThat(responses.get(0).getUsername()).isEqualTo(list.get(0));
            assertThat(responses.get(1).getUsername()).isEqualTo(list.get(1));
        }

        @Test
        @DisplayName("viewCount 기준 내림차순 정렬이 정상적으로 적용된다")
        void shouldSortProfilesByViewCountDesc() {
            // given
            PageRequest pageable = PageRequest.of(0, 5, Sort.by("viewCount").descending());

            // when
            Page<ProfileResponse> page = profileRepositoryCustomImpl.findProfiles(pageable);
            List<ProfileResponse> responses = page.getContent();

            // then
            assertThat(responses.get(0).getViewCount()).isEqualTo(29);
            assertThat(responses.get(1).getViewCount()).isEqualTo(28);
        }

        @Test
        @DisplayName("createdAt 기준 내림차순 정렬이 기본으로 적용된다")
        void shouldSortProfilesByCreatedAtDesc() {
            // given
            PageRequest pageable = PageRequest.of(0, 5);

            // when
            Page<ProfileResponse> page = profileRepositoryCustomImpl.findProfiles(pageable);
            List<ProfileResponse> responses = page.getContent();

            // then
            assertThat(responses.get(0).getUsername()).isEqualTo(names[names.length-1]);
        }
        @Test
        @DisplayName("프로필이 없을 경우는 빈 리스트를 반환한다")
        void shouldReturnEmptyListWhenNoProfiles() {
            // given
            profileRepository.deleteAll();
            PageRequest pageable = PageRequest.of(0, 5);

            // when
            Page<ProfileResponse> page = profileRepositoryCustomImpl.findProfiles(pageable);

            // then
            assertThat(page.getContent()).isEmpty();
            assertThat(page.getTotalElements()).isZero();
        }
    }

    @Nested
    @DisplayName("프로필 아이디로 조회")
    class FindProfileById{
        @Test
        @DisplayName("프로필이 존재할 경우")
        void testFindProfileById() {
            // given
            Profile profile = profiles.get(0);
            // when
            var result = profileRepositoryCustomImpl.findProfileById(profile.getId());
            // then
            assertThat(result).isPresent();
            assertThat(result.get().getTitle()).isEqualTo(profile.getTitle());
            assertThat(result.get().getContent()).isEqualTo(profile.getContent());
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
