package com.plus.profile.user.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plus.profile.global.exception.BusinessException;
import com.plus.profile.point.domain.UserCoupon;
import com.plus.profile.user.application.UserService;
import com.plus.profile.user.exception.UserExceptionCode;
import com.plus.profile.user.presentation.dto.UserCouponResponse;
import com.plus.profile.user.presentation.dto.UserDetailResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(UserController.class)
@AutoConfigureRestDocs
class UserControllerTest {

    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Nested
    @DisplayName("GET /api/v1/users/{userId}")
    class GetUserDetail {

        @Test
        @DisplayName("정상적으로 유저 디테일을 조회한다")
        void shouldReturnUserDetailSuccessfully() throws Exception {
            // given
            UUID userId = UUID.randomUUID();
            List<UserCouponResponse> coupons = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                UserCouponResponse userCouponResponse = new UserCouponResponse(
                        (long) i,
                        UUID.randomUUID(),
                        "쿠폰명" + i,
                        1000L,
                        LocalDateTime.now().plusDays(i+1),
                        "쿠폰 설명" + i
                );
                coupons.add(userCouponResponse);
            }
            UserDetailResponse mockResponse = new UserDetailResponse(
                    userId,
                    "testuser",
                    "USER",
                    10000L,
                    coupons
            );

            given(userService.getUserDetail(userId))
                    .willReturn(mockResponse);

            // when & then
            mockMvc.perform(get("/api/v1/users/{userId}", userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.statusCode").value(200))
                    .andExpect(jsonPath("$.timestamp").isNotEmpty())
                    .andExpect(jsonPath("$.data.id").value(userId.toString()))
                    .andExpect(jsonPath("$.data.username").value("testuser"))
                    .andExpect(jsonPath("$.data.role").value("USER"))
                    .andExpect(jsonPath("$.data.point").value(10000))
                    .andExpect(jsonPath("$.data.coupons").isArray())
                    .andExpect(jsonPath("$.data.coupons.size()").value(3))
                    .andExpect(jsonPath("$.data.coupons[0].id").value(0))
                    .andExpect(jsonPath("$.data.coupons[0].couponId").isNotEmpty())
                    .andExpect(jsonPath("$.data.coupons[0].couponCode").value("쿠폰명0"))
                    .andExpect(jsonPath("$.data.coupons[0].discountAmount").value(1000))
                    .andExpect(jsonPath("$.data.coupons[0].expirationDate").isNotEmpty())
                    .andExpect(jsonPath("$.data.coupons[0].description").value("쿠폰 설명0"))

                    .andDo(document("user-detail",
                            responseFields(
                                    fieldWithPath("success").description("요청 성공 여부"),
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("timestamp").description("요청 시각"),
                                    fieldWithPath("data.id").description("유저 ID"),
                                    fieldWithPath("data.username").description("유저 이름"),
                                    fieldWithPath("data.role").description("유저 역할"),
                                    fieldWithPath("data.point").description("유저 포인트"),
                                    fieldWithPath("data.coupons").description("유저 쿠폰 목록"),
                                    fieldWithPath("data.coupons[].id").type(JsonFieldType.NUMBER).description("유저 쿠폰 ID"),
                                    fieldWithPath("data.coupons[].couponId").type(JsonFieldType.STRING).description("쿠폰 ID"),
                                    fieldWithPath("data.coupons[].couponCode").type(JsonFieldType.STRING).description("쿠폰 코드"),
                                    fieldWithPath("data.coupons[].discountAmount").type(JsonFieldType.NUMBER).description("할인 금액"),
                                    fieldWithPath("data.coupons[].expirationDate").type(JsonFieldType.STRING).description("쿠폰 만료일"),
                                    fieldWithPath("data.coupons[].description").type(JsonFieldType.STRING).description("쿠폰 설명")
                            )
                    ));
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
