package com.plus.profile.user.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.plus.profile.global.exception.BusinessException;
import com.plus.profile.user.application.UserService;
import com.plus.profile.user.exception.UserExceptionCode;
import com.plus.profile.user.presentation.dto.UserDetailResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("GET /api/v1/users/{userId}")
    class GetUserDetail {

        @Test
        @DisplayName("정상적으로 유저 디테일을 조회한다")
        void shouldReturnUserDetailSuccessfully() throws Exception {
            // given
            UUID userId = UUID.randomUUID();
            UserDetailResponse mockResponse = new UserDetailResponse(
                    userId,
                    "testuser",
                    "USER",
                    10000L,
                    Collections.emptyList()
            );

            given(userService.getUserDetail(userId))
                    .willReturn(mockResponse);

            // when & then
            mockMvc.perform(get("/api/v1/users/{userId}", userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(userId.toString()))
                    .andExpect(jsonPath("$.data.username").value("testuser"))
                    .andExpect(jsonPath("$.data.role").value("USER"))
                    .andExpect(jsonPath("$.data.point").value(10000));
        }
        @Test
        @DisplayName("존재하지 않는 유저를 조회할 때 404 에러를 반환한다")
        void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
            // given
            UUID userId = UUID.randomUUID();
            given(userService.getUserDetail(userId))
                    .willThrow(new BusinessException(UserExceptionCode.USER_NOT_FOUND));

            // when & then
            mockMvc.perform(get("/api/v1/users/{userId}", userId))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("User not found"));
        }
    }


}
