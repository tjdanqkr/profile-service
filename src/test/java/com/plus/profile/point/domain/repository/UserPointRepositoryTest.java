package com.plus.profile.point.domain.repository;

import com.plus.profile.user.domain.User;
import com.plus.profile.point.domain.UserPoint;
import com.plus.profile.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserPointRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserPointRepository userPointRepository;
    private User user;
    private UserPoint userPoint;
    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("testUser")
                .encodedPassword("testPassword")
                .build();
        userRepository.save(user);
        userPoint = UserPoint.builder().user(user).point(0).build();
        userPointRepository.save(userPoint);

    }
    @Nested
    @DisplayName("유저 아이디로 찾아오기")
    class FindByUserId {
        @Test
        @DisplayName("있는 유저 아이디로 포인트를 찾을 때 Optional<UserPoint>를 반환한다.")
        void shouldReturnOptionalUserPoint() {
            // Given
            UUID userId = user.getId();
            // When
            Optional<UserPoint> byUserId = userPointRepository.findByUserId(userId);
            // Then
            assertTrue(byUserId.isPresent());
            assertEquals(userId, byUserId.get().getUser().getId());
        }

        @Test
        @DisplayName("없는 유저 아이디로 포인트를 찾을 때 Optional.empty()를 반환한다.")
        void shouldReturnOptionalNull() {
            // Given
            UUID userId = UUID.randomUUID();
            // When
            Optional<UserPoint> byUserId = userPointRepository.findByUserId(userId);
            // Then
            assertTrue(byUserId.isEmpty());
        }
    }
}