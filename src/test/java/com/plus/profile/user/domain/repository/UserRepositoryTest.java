package com.plus.profile.user.domain.repository;

import com.plus.profile.user.domain.User;
import com.plus.profile.user.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    User user;
    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("testUser")
                .encodedPassword("testPassword")
                .build();
        userRepository.save(user);
    }
    @Nested
    @DisplayName("username으로 유저를 찾을 때")
    class FindByUsername {
        @Test
        @DisplayName("username으로 유저를 찾을 수 있다.")
        void findByUsername() {
            // given
            String username = user.getUsername();

            // when
            Optional<User> byUsername = userRepository.findByUsername(username);

            // then
            assertTrue(byUsername.isPresent());
            assertEquals(username, byUsername.get().getUsername());
        }
        @Test
        @DisplayName("없는 유저를 찾을떄 Optional null을 반환한다.")
        void findByUsernameNotFound() {
            // given
            String username = "nonExistentUser";

            // when
            Optional<User> byUsername = userRepository.findByUsername(username);

            // then
            assertTrue(byUsername.isEmpty());
        }
    }
}