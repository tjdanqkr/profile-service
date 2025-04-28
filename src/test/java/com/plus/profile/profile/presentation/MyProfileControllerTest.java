package com.plus.profile.profile.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plus.profile.global.exception.BusinessException;
import com.plus.profile.profile.application.ProfileService;
import com.plus.profile.profile.domain.MyProfile;
import com.plus.profile.profile.exception.ProfileExceptionCode;
import com.plus.profile.profile.presentation.dto.ProfileDetailResponse;
import com.plus.profile.profile.presentation.dto.ProfileResponse;
import com.plus.profile.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(ProfileController.class)
class MyProfileControllerTest {

    private MockMvc mockMvc;

    @MockitoBean
    private ProfileService profileService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Nested
    @DisplayName("GET /api/v1/profiles")
    class GetProfiles {

        @Test
        @DisplayName("프로필 목록을 정상적으로 반환한다")
        void shouldReturnProfilesSuccessfully() throws Exception {
            // given
            List<ProfileResponse> profileList = new ArrayList<>();
            for (int i = 1; i < 3; i++) {
                ProfileResponse response = new ProfileResponse(
                        UUID.randomUUID(),
                        "Title" + i,
                        10L * i,
                        LocalDateTime.now(),
                        UUID.randomUUID(),
                        "user" + i
                );
                profileList.add(response);
            }


            Page<ProfileResponse> mockPage = new PageImpl<>(profileList, PageRequest.of(0, 10), 2);

            given(profileService.getProfiles(any())).willReturn(mockPage);

            // when & then
            mockMvc.perform(get("/api/v1/profiles")
                            .param("page", "0")
                            .param("size", "10")
                            .param("sort", "createdAt,DESC")
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.statusCode").value(200))
                    .andExpect(jsonPath("$.timestamp").isNotEmpty())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content.length()").value(2))
                    .andExpect(jsonPath("$.data.content[0].username").value("user1"))
                    .andExpect(jsonPath("$.data.content[1].username").value("user2"))
                    .andExpect(jsonPath("$.data.content[0].createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.data.content[0].profileId").isString())
                    .andExpect(jsonPath("$.data.content[0].viewCount").isNumber())
                    .andExpect(jsonPath("$.data.content[0].title").isString())
                    .andExpect(jsonPath("$.data.content[0].userId").isString())
                    .andExpect(jsonPath("$.data.page.size").value(10))
                    .andExpect(jsonPath("$.data.page.totalPages").value(1))
                    .andExpect(jsonPath("$.data.page.totalElements").value(2))
                    .andDo(document("profiles-get",
                            queryParameters(
                                    parameterWithName("page").description("페이지 번호"),
                                    parameterWithName("size").description("페이지 사이즈"),
                                    parameterWithName("sort").description("정렬 기준 (createdAt DESC, viewCount DESC, username ASC) - 기본값: createdAt, DESC").optional()
                            ),
                            responseFields(
                                    fieldWithPath("success").description("요청 성공 여부"),
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("timestamp").description("요청 처리 시간"),
                                    fieldWithPath("data.content[].profileId").description("프로필 ID"),
                                    fieldWithPath("data.content[].username").description("유저 이름"),
                                    fieldWithPath("data.content[].title").description("프로필 제목"),
                                    fieldWithPath("data.content[].viewCount").description("조회수"),
                                    fieldWithPath("data.content[].createdAt").description("프로필 생성 시간"),
                                    fieldWithPath("data.content[].userId").description("유저 ID"),
                                    fieldWithPath("data.content[].createdAt").description("프로필 생성 시간"),
                                    fieldWithPath("data.page.size").description("페이지 사이즈"),
                                    fieldWithPath("data.page.totalPages").description("총 페이지 수"),
                                    fieldWithPath("data.page.totalElements").description("총 요소 수"),
                                    fieldWithPath("data.page.number").description("현재 페이지 번호")
                            )
                    ));
        }
        @Test
        @DisplayName("프로필 목록을 정상적으로 반환한다 - 페이지 정보가 없는 경우")
        void shouldReturnProfilesSuccessfullyWithoutPageInfo() throws Exception {
            // given
            List<ProfileResponse> profileList = new ArrayList<>();
            for (int i = 1; i < 3; i++) {
                ProfileResponse response = new ProfileResponse(
                        UUID.randomUUID(),
                        "Title" + i,
                        10L * i,
                        LocalDateTime.now(),
                        UUID.randomUUID(),
                        "user" + i
                );
                profileList.add(response);
            }
            Page<ProfileResponse> mockPage = new PageImpl<>(profileList, PageRequest.of(0, 10), 2);

            given(profileService.getProfiles(any())).willReturn(mockPage);

            // when & then
            mockMvc.perform(get("/api/v1/profiles"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content.length()").value(2))
                    .andExpect(jsonPath("$.data.content[0].username").value("user1"))
                    .andExpect(jsonPath("$.data.content[1].username").value("user2"));
        }
        @Test
        @DisplayName("페이지 사이즈가 1000초과하면 400을 반환한다")
        void shouldReturnBadRequestWhenPageSizeExceedsLimit() throws Exception {
            // given
            int pageSize = 1001;
            given(profileService.getProfiles(any(Pageable.class)))
                    .willThrow(new BusinessException(ProfileExceptionCode.PAGE_SIZE_TOO_LARGE));
            // when & then
            mockMvc.perform(get("/api/v1/profiles")
                            .param("page", "0")
                            .param("size", String.valueOf(pageSize))
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Page size too large"))
                    .andExpect(jsonPath("$.statusCode").value(400));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/profiles/{profileId}")
    class GetMyProfileDetail {

        @Test
        @DisplayName("프로필 상세 정보를 정상적으로 반환한다")
        void shouldReturnProfileDetail() throws Exception {
            // given
            UUID profileId = UUID.randomUUID();
            ProfileDetailResponse response = new ProfileDetailResponse(profileId, "title", "content", 100L, LocalDateTime.now(), UUID.randomUUID(), "testuser");

            given(profileService.getProfileDetail(profileId)).willReturn(response);

            // when & then
            mockMvc.perform(get("/api/v1/profiles/{profileId}", profileId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.statusCode").value(200))
                    .andExpect(jsonPath("$.data.profileId").value(profileId.toString()))
                    .andExpect(jsonPath("$.data.title").value("title"))
                    .andExpect(jsonPath("$.data.content").value("content"))
                    .andExpect(jsonPath("$.data.viewCount").isNumber())
                    .andExpect(jsonPath("$.data.userId").isString())
                    .andExpect(jsonPath("$.data.username").isString())
                    .andExpect(jsonPath("$.data.createdAt").isNotEmpty())
                    .andDo(document("profiles-get-detail",
                            responseFields(
                                    fieldWithPath("success").description("요청 성공 여부"),
                                    fieldWithPath("data.profileId").description("프로필 ID"),
                                    fieldWithPath("data.title").description("프로필 제목"),
                                    fieldWithPath("data.content").description("프로필 내용"),
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("timestamp").description("요청 처리 시간"),
                                    fieldWithPath("data.viewCount").description("조회수"),
                                    fieldWithPath("data.userId").description("유저 ID"),
                                    fieldWithPath("data.username").description("유저 이름"),
                                    fieldWithPath("data.createdAt").description("프로필 생성 시간")
                            )
                    ));
        }
        @Test
        @DisplayName("존재하지 않는 프로필 ID로 조회 시 404 에러를 반환한다")
        void shouldReturnNotFound() throws Exception {
            // given
            UUID profileId = UUID.randomUUID();
            given(profileService.getProfileDetail(profileId))
                    .willThrow(new BusinessException(ProfileExceptionCode.PROFILE_NOT_FOUND));

            // when & then
            mockMvc.perform(get("/api/v1/profiles/{profileId}", profileId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Profile not found"))
                    .andExpect(jsonPath("$.statusCode").value(404));
        }
    }
}
