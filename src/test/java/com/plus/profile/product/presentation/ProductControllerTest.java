package com.plus.profile.product.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plus.profile.global.exception.BusinessException;
import com.plus.profile.product.application.ProductPurchaseService;
import com.plus.profile.product.exception.ProductExceptionCode;
import com.plus.profile.product.presentation.dto.ProductPurchaseRequest;
import com.plus.profile.product.presentation.dto.ProductPurchaseResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductPurchaseService productPurchaseService;

    @Autowired
    private ObjectMapper objectMapper;

    private final UUID userId = UUID.randomUUID();
    private final Long productId = 1L;

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
            Mockito.when(productPurchaseService.productPurchase(eq(userId), eq(productId)))
                    .thenReturn(mockResponse);

            // when, then
            mockMvc.perform(post("/api/v1/products/{productId}/purchase", productId)
                            .param("userId", userId.toString())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @DisplayName("구매 실패 - 포인트 부족")
        void purchaseProduct_fail_insufficientPoints() throws Exception {
            // given
            Mockito.when(productPurchaseService.productPurchase(eq(userId), eq(productId)))
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
            Mockito.when(productPurchaseService.productPurchaseWithCoupon(eq(userId), eq(productId), any(ProductPurchaseRequest.class)))
                    .thenReturn(mockResponse);

            // when, then
            mockMvc.perform(post("/api/v1/products/{productId}/purchase-with-coupon", productId)
                            .param("userId", userId.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
        @Test
        @DisplayName("구매 실패 - 포인트 부족")
        void purchaseProductWithCoupon_fail_insufficientPoints() throws Exception {
            // given
            ProductPurchaseRequest request = new ProductPurchaseRequest(100L);
            Mockito.when(productPurchaseService.productPurchaseWithCoupon(eq(userId), eq(productId), any(ProductPurchaseRequest.class)))
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
            Mockito.when(productPurchaseService.productPurchaseWithCoupon(eq(userId), eq(productId), any(ProductPurchaseRequest.class)))
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
            Mockito.when(productPurchaseService.productPurchaseWithCoupon(eq(userId), eq(productId), any(ProductPurchaseRequest.class)))
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
            Mockito.when(productPurchaseService.productPurchaseWithCoupon(eq(userId), eq(productId), any(ProductPurchaseRequest.class)))
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
