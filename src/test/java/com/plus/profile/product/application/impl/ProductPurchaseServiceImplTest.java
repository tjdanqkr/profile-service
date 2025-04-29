package com.plus.profile.product.application.impl;

import com.plus.profile.global.dto.point.PayOffPointRequest;
import com.plus.profile.global.dto.point.PayOffPointResponse;
import com.plus.profile.global.dto.point.PayOffResultType;
import com.plus.profile.global.exception.BusinessException;
import com.plus.profile.product.application.ProductPointClientService;
import com.plus.profile.product.domain.Product;
import com.plus.profile.product.domain.repository.ProductRepository;
import com.plus.profile.product.exception.ProductExceptionCode;
import com.plus.profile.product.presentation.dto.ProductPurchaseRequest;
import com.plus.profile.product.presentation.dto.ProductPurchaseResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductPurchaseServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductPointClientService productPointClientService;

    @InjectMocks
    private ProductPurchaseServiceImpl productPurchaseService;

    @Nested
    @DisplayName("productPurchase 메서드 테스트")
    class ProductPurchase {

        @Test
        @DisplayName("정상적으로 상품을 포인트로 구매한다")
        void purchaseProductSuccess() {
            // given
            UUID userId = UUID.randomUUID();
            Long productId = 1L;
            Product product = Product.builder()
                    .id(productId)
                    .name("상품1")
                    .price(5000L)
                    .build();

            when(productRepository.findById(productId)).thenReturn(Optional.of(product));
            when(productPointClientService.payOffPoint(any(PayOffPointRequest.class)))
                    .thenReturn(new PayOffPointResponse(true, "success", PayOffResultType.SUCCESS, 5000L, 5000L));

            // when
            ProductPurchaseResponse response = productPurchaseService.productPurchase(userId, productId);

            // then
            assertThat(response).isNotNull();
            assertThat(response.productId()).isEqualTo(productId);
            assertThat(response.productName()).isEqualTo("상품1");
            assertThat(response.originalPrice()).isEqualTo(5000L);
            assertThat(response.discountAmount()).isZero();
            assertThat(response.finalPrice()).isEqualTo(5000L);
            assertThat(response.remainingPoints()).isEqualTo(5000L);

            verify(productRepository, times(1)).findById(productId);
            verify(productPointClientService, times(1)).payOffPoint(any(PayOffPointRequest.class));
        }
        @Test
        @DisplayName("유저가 존재하지 않으면 예외 발생")
        void userNotFound() {
            // given
            UUID userId = UUID.randomUUID();
            Long productId = 1L;
            Product product = Product.builder()
                    .id(productId)
                    .name("상품1")
                    .price(5000L)
                    .build();

            when(productRepository.findById(productId)).thenReturn(Optional.of(product));
            when(productPointClientService.payOffPoint(any(PayOffPointRequest.class)))
                    .thenReturn(new PayOffPointResponse(false, PayOffResultType.USER_NOT_FOUND.getMessage(), PayOffResultType.USER_NOT_FOUND, 0, 0));


            // when & then
            assertThatThrownBy(() ->
                    productPurchaseService.productPurchase(userId, productId)
            ).isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ProductExceptionCode.USER_NOT_FOUND.getMessage());
        }
        @Test
        @DisplayName("상품이 존재하지 않으면 예외 발생")
        void productNotFound() {
            // given
            UUID userId = UUID.randomUUID();
            Long productId = 1L;

            when(productRepository.findById(productId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    productPurchaseService.productPurchase(userId, productId)
            ).isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ProductExceptionCode.PRODUCT_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("포인트 부족 시 예외 발생")
        void insufficientPoints() {
            // given
            UUID userId = UUID.randomUUID();
            Long productId = 1L;
            Product product = Product.builder()
                    .id(productId)
                    .name("상품1")
                    .price(5000L)
                    .build();

            when(productRepository.findById(productId)).thenReturn(Optional.of(product));
            when(productPointClientService.payOffPoint(any(PayOffPointRequest.class)))
                    .thenReturn(new PayOffPointResponse(false, PayOffResultType.NOT_ENOUGH_POINTS.getMessage(), PayOffResultType.NOT_ENOUGH_POINTS, 3000L, 0));


            // when & then
            assertThatThrownBy(() ->
                    productPurchaseService.productPurchase(userId, productId)
            ).isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ProductExceptionCode.NOT_ENOUGH_POINTS.getMessage());
        }

        @Test
        @DisplayName("시스템 오류 시 예외 발생")
        void systemError() {
            // given
            UUID userId = UUID.randomUUID();
            Long productId = 1L;
            Product product = Product.builder()
                    .id(productId)
                    .name("상품1")
                    .price(5000L)
                    .build();

            when(productRepository.findById(productId)).thenReturn(Optional.of(product));
            when(productPointClientService.payOffPoint(any(PayOffPointRequest.class)))
                    .thenReturn(new PayOffPointResponse(false, PayOffResultType.SYSTEM_ERROR.getMessage(), PayOffResultType.SYSTEM_ERROR, 3000L, 0));


            // when & then
            assertThatThrownBy(() ->
                    productPurchaseService.productPurchase(userId, productId)
            ).isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ProductExceptionCode.SYSTEM_ERROR.getMessage());
        }
    }
    @Nested
    @DisplayName("productPurchaseWithCoupon 테스트")
    class ProductPurchaseWithCouponTest {

        @Test
        @DisplayName("쿠폰 사용하여 상품 구매 성공")
        void purchaseWithCouponSuccess() {
            // given
            UUID userId = UUID.randomUUID();
            Product product = Product.builder()
                    .id(1L)
                    .name("테스트 상품")
                    .price(10000L)
                    .build();
            when(productRepository.findById(product.getId()))
                    .thenReturn(Optional.of(product));

            when(productPointClientService.payOffPointWithCoupon(any()))
                    .thenReturn(new PayOffPointResponse(
                            true, "success", PayOffResultType.SUCCESS, 6000L , 4000L

                    ));

            ProductPurchaseRequest request = new ProductPurchaseRequest(1L);

            // when
            ProductPurchaseResponse response = productPurchaseService.productPurchaseWithCoupon(userId, product.getId(), request);

            // then
            assertThat(response).isNotNull();
            assertThat(response.productId()).isEqualTo(product.getId());
            assertThat(response.productName()).isEqualTo(product.getName());
            assertThat(response.originalPrice()).isEqualTo(10000L);
            assertThat(response.discountAmount()).isEqualTo(6000L);
            assertThat(response.finalPrice()).isEqualTo(4000L);
            assertThat(response.remainingPoints()).isEqualTo(6000L);


            verify(productRepository).findById(product.getId());
            verify(productPointClientService).payOffPointWithCoupon(any());
        }

        @Test
        @DisplayName("쿠폰 사용 중 상품 없음 예외 발생")
        void purchaseWithCouponProductNotFound() {
            // given
            UUID userId = UUID.randomUUID();
            Product product = Product.builder()
                    .id(1L)
                    .name("테스트 상품")
                    .price(10000L)
                    .build();
            when(productRepository.findById(product.getId()))
                    .thenReturn(Optional.empty());

            ProductPurchaseRequest request = new ProductPurchaseRequest( 1L);

            // when & then
            assertThatThrownBy(() -> productPurchaseService.productPurchaseWithCoupon(userId, product.getId(), request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ProductExceptionCode.PRODUCT_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("쿠폰 사용 중 포인트 부족 예외 발생")
        void purchaseWithCouponNotEnoughPoints() {
            // given
            UUID userId = UUID.randomUUID();
            Product product = Product.builder()
                    .id(1L)
                    .name("테스트 상품")
                    .price(10000L)
                    .build();
            when(productRepository.findById(product.getId()))
                    .thenReturn(Optional.of(product));

            when(productPointClientService.payOffPointWithCoupon(any()))
                    .thenReturn(new PayOffPointResponse(false, "잔액 부족", PayOffResultType.NOT_ENOUGH_POINTS, 3000L, 0));


            ProductPurchaseRequest request = new ProductPurchaseRequest(1L);

            // when & then
            assertThatThrownBy(() -> productPurchaseService.productPurchaseWithCoupon(userId, product.getId(), request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ProductExceptionCode.NOT_ENOUGH_POINTS.getMessage());
        }

        @Test
        @DisplayName("쿠폰 사용 중 쿠폰 없음 예외 발생")
        void purchaseWithCouponNotFoundCoupon() {
            // given
            UUID userId = UUID.randomUUID();
            Product product = Product.builder()
                    .id(1L)
                    .name("테스트 상품")
                    .price(10000L)
                    .build();
            when(productRepository.findById(product.getId()))
                    .thenReturn(Optional.of(product));

            when(productPointClientService.payOffPointWithCoupon(any()))
                    .thenReturn(new PayOffPointResponse(false, "쿠폰 없음", PayOffResultType.COUPON_NOT_FOUND, 0, 0));


            ProductPurchaseRequest request = new ProductPurchaseRequest(1L);

            // when & then
            assertThatThrownBy(() -> productPurchaseService.productPurchaseWithCoupon(userId, product.getId(), request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ProductExceptionCode.COUPON_NOT_FOUND.getMessage());
        }
    }
}
