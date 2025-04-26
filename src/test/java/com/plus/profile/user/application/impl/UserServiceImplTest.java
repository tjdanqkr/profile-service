package com.plus.profile.user.application.impl;

import com.plus.profile.global.exception.BusinessException;
import com.plus.profile.user.exception.UserExceptionCode;
import com.plus.profile.user.infra.UserRepositoryCustom;
import com.plus.profile.user.presentation.dto.UserDetailResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepositoryCustom userRepositoryCustom;

    @InjectMocks
    private UserServiceImpl userServiceImpl;


    @Nested
    @DisplayName("getUserDetail 메서드 테스트")
    class GetUserDetail {

        @Test
        @DisplayName("유저 디테일 조회 성공")
        void shouldReturnUserDetailSuccessfully() {
            // given
            UUID userId = UUID.randomUUID();
            UserDetailResponse mockResponse = mock(UserDetailResponse.class);

            given(userRepositoryCustom.findUserDetailById(userId))
                    .willReturn(Optional.of(mockResponse));

            // when
            UserDetailResponse result = userServiceImpl.getUserDetail(userId);

            // then
            assertThat(result).isNotNull();
            verify(userRepositoryCustom).findUserDetailById(userId);
        }

        @Test
        @DisplayName("존재하지 않는 유저 조회 시 BusinessException 발생")
        void shouldThrowBusinessExceptionWhenUserNotFound() {
            // given
            UUID userId = UUID.randomUUID();
            given(userRepositoryCustom.findUserDetailById(userId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userServiceImpl.getUserDetail(userId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(UserExceptionCode.USER_NOT_FOUND.getMessage());

            verify(userRepositoryCustom).findUserDetailById(userId);
        }
    }
}
