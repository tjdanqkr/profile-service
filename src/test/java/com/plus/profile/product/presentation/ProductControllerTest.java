package com.plus.profile.product.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plus.profile.global.exception.BusinessException;
import com.plus.profile.product.application.ProductPurchaseService;
import com.plus.profile.product.application.ProductService;
import com.plus.profile.product.exception.ProductExceptionCode;
import com.plus.profile.product.presentation.dto.ProductPurchaseRequest;
import com.plus.profile.product.presentation.dto.ProductPurchaseResponse;
import com.plus.profile.product.presentation.dto.ProductResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(ProductController.class)
class ProductControllerTest {


    private MockMvc mockMvc;

    @MockitoBean
    private ProductPurchaseService productPurchaseService;

    @MockitoBean
    private ProductService productService;


    @Autowired
    private ObjectMapper objectMapper;

    private final UUID userId = UUID.randomUUID();
    private final Long productId = 1L;
    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }


    @Test
    void getProductList() throws Exception {
        // given
        List<ProductResponse> responses = List.of(new ProductResponse(productId, "상품명", 10000L));
        when(productService.getAllProducts(any(Pageable.class)))
                .thenReturn(new PageImpl<>(responses, PageRequest.of(0,10), 1));
        // when, then
        mockMvc.perform(get("/api/v1/products")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.data.page.size").value(10))
                .andExpect(jsonPath("$.data.page.totalPages").value(1))
                .andExpect(jsonPath("$.data.page.totalElements").value(1))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].id").value(productId))
                .andExpect(jsonPath("$.data.content[0].name").value("상품명"))
                .andExpect(jsonPath("$.data.content[0].price").value(10000L))
                .andDo(document("product-list",
                        queryParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("페이지 사이즈")
                        ),
                        responseFields(
                                fieldWithPath("success").description("요청 성공 여부"),
                                fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                fieldWithPath("timestamp").description("요청 처리 시간"),
                                fieldWithPath("data.page.size").description("페이지 사이즈"),
                                fieldWithPath("data.page.totalPages").description("총 페이지 수"),
                                fieldWithPath("data.page.totalElements").description("총 요소 수"),
                                fieldWithPath("data.page.number").description("현재 페이지 번호"),
                                fieldWithPath("data.content").description("상품 목록"),
                                fieldWithPath("data.content[].id").description("상품 ID"),
                                fieldWithPath("data.content[].name").description("상품 이름"),
                                fieldWithPath("data.content[].price").description("상품 가격")
                        )
                ));
    }


    @Nested
    @DisplayName("포인트로 상품 구매 API")
    class PurchaseProduct {

        @Test
        @DisplayName("성공적으로 상품을 포인트로 구매한다")
        void purchaseProduct_success() throws Exception {
            // given
            ProductPurchaseResponse mockResponse = new ProductPurchaseResponse(
                    productId, "상품명", 10000L, 0L, 10000L, 90000L
            );

            when(productPurchaseService.productPurchase(userId, productId))

                    .thenReturn(mockResponse);

            // when, then
            mockMvc.perform(post("/api/v1/products/{productId}/purchase", productId)
                            .param("userId", userId.toString())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.statusCode").value(200))
                    .andExpect(jsonPath("$.timestamp").isNotEmpty())
                    .andExpect(jsonPath("$.data.productId").value(productId))
                    .andExpect(jsonPath("$.data.productName").value("상품명"))
                    .andExpect(jsonPath("$.data.originalPrice").value(10000L))
                    .andExpect(jsonPath("$.data.discountAmount").value(0L))
                    .andExpect(jsonPath("$.data.finalPrice").value(10000L))
                    .andExpect(jsonPath("$.data.productId").value(productId))
                    .andExpect(jsonPath("$.data.remainingPoints").value(90000L))
                    .andDo(document("product-purchase",
                            responseFields(
                                    fieldWithPath("success").description("요청 성공 여부"),
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("timestamp").description("요청 처리 시간"),
                                    fieldWithPath("data.productId").description("상품 ID"),
                                    fieldWithPath("data.productName").description("상품 이름"),
                                    fieldWithPath("data.originalPrice").description("상품 원래 가격"),
                                    fieldWithPath("data.discountAmount").description("할인 금액"),
                                    fieldWithPath("data.finalPrice").description("최종 가격"),
                                    fieldWithPath("data.remainingPoints").description("남은 포인트")
                            )
                    ));
        }

        @Test
        @DisplayName("구매 실패 - 포인트 부족")
        void purchaseProduct_fail_insufficientPoints() throws Exception {
            // given

            when(productPurchaseService.productPurchase(userId, productId))

                    .thenThrow(new BusinessException(ProductExceptionCode.NOT_ENOUGH_POINTS));

            // when, then
            mockMvc.perform(post("/api/v1/products/{productId}/purchase", productId)
                            .param("userId", userId.toString())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("포인트 + 쿠폰으로 상품 구매 API")
    class PurchaseProductWithCoupon {

        @Test
        @DisplayName("성공적으로 상품을 포인트+쿠폰으로 구매한다")
        void purchaseProductWithCoupon_success() throws Exception {
            // given
            ProductPurchaseRequest request = new ProductPurchaseRequest(100L);
            ProductPurchaseResponse mockResponse = new ProductPurchaseResponse(
                    productId, "상품명", 10000L, 2000L, 8000L, 92000L
            );

            when(productPurchaseService.productPurchaseWithCoupon(userId, productId, request))

                    .thenReturn(mockResponse);

            // when, then
            mockMvc.perform(post("/api/v1/products/{productId}/purchase-with-coupon", productId)
                            .param("userId", userId.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.statusCode").value(200))
                    .andExpect(jsonPath("$.timestamp").isNotEmpty())
                    .andExpect(jsonPath("$.data.productId").value(productId))
                    .andExpect(jsonPath("$.data.productName").value("상품명"))
                    .andExpect(jsonPath("$.data.originalPrice").value(10000L))
                    .andExpect(jsonPath("$.data.discountAmount").value(2000L))
                    .andExpect(jsonPath("$.data.finalPrice").value(8000L))
                    .andExpect(jsonPath("$.data.remainingPoints").value(92000L))
                    .andDo(document("product-purchase-with-coupon",
                            queryParameters(
                                    parameterWithName("userId").description("유저 ID")
                            ),
                            requestFields(
                                    fieldWithPath("userCouponId").description("유저 쿠폰 ID")
                            ),
                            responseFields(
                                    fieldWithPath("success").description("요청 성공 여부"),
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("timestamp").description("요청 처리 시간"),
                                    fieldWithPath("data.productId").description("상품 ID"),
                                    fieldWithPath("data.productName").description("상품 이름"),
                                    fieldWithPath("data.originalPrice").description("상품 원래 가격"),
                                    fieldWithPath("data.discountAmount").description("할인 금액"),
                                    fieldWithPath("data.finalPrice").description("최종 가격"),
                                    fieldWithPath("data.remainingPoints").description("남은 포인트")
                            )
                    ));
        }
        @Test
        @DisplayName("구매 실패 - 포인트 부족")
        void purchaseProductWithCoupon_fail_insufficientPoints() throws Exception {
            // given
            ProductPurchaseRequest request = new ProductPurchaseRequest(100L);

            when(productPurchaseService.productPurchaseWithCoupon(userId, productId, request))

                    .thenThrow(new BusinessException(ProductExceptionCode.NOT_ENOUGH_POINTS));

            // when, then
            mockMvc.perform(post("/api/v1/products/{productId}/purchase-with-coupon", productId)
                            .param("userId", userId.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
        @Test
        @DisplayName("구매 실패 - 쿠폰 없음")
        void purchaseProductWithCoupon_fail_couponNotFound() throws Exception {
            // given
            ProductPurchaseRequest request = new ProductPurchaseRequest(100L);
            when(productPurchaseService.productPurchaseWithCoupon(userId, productId, request))

                    .thenThrow(new BusinessException(ProductExceptionCode.COUPON_NOT_FOUND));

            // when, then
            mockMvc.perform(post("/api/v1/products/{productId}/purchase-with-coupon", productId)
                            .param("userId", userId.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        @DisplayName("구매 실패 - 쿠폰 이미 사용")
        void purchaseProductWithCoupon_fail_couponAlreadyUsed() throws Exception {
            // given
            ProductPurchaseRequest request = new ProductPurchaseRequest(100L);

            when(productPurchaseService.productPurchaseWithCoupon(userId, productId, request))

                    .thenThrow(new BusinessException(ProductExceptionCode.COUPON_ALREADY_USED));

            // when, then
            mockMvc.perform(post("/api/v1/products/{productId}/purchase-with-coupon", productId)
                            .param("userId", userId.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        @DisplayName("구매 실패 - 쿠폰 만료")
        void purchaseProductWithCoupon_fail_couponExpired() throws Exception {
            // given
            ProductPurchaseRequest request = new ProductPurchaseRequest(100L);

            when(productPurchaseService.productPurchaseWithCoupon(userId, productId, request))

                    .thenThrow(new BusinessException(ProductExceptionCode.COUPON_EXPIRED));

            // when, then
            mockMvc.perform(post("/api/v1/products/{productId}/purchase-with-coupon", productId)
                            .param("userId", userId.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }
}
