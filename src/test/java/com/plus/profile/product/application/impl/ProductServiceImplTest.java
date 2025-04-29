package com.plus.profile.product.application.impl;

import com.plus.profile.product.application.ProductService;
import com.plus.profile.product.infra.ProductRepositoryCustom;
import com.plus.profile.product.presentation.dto.ProductResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    @InjectMocks
    private ProductServiceImpl productService;
    @Mock
    private ProductRepositoryCustom productRepositoryCustom;
    @Test
    void getAllProducts() {
        // Given
        Long productId = 1L;
        List<ProductResponse> responses = List.of(new ProductResponse(productId, "상품명", 10000L));
        PageRequest pageable = PageRequest.of(0, 10);
        when(productRepositoryCustom.findAllProducts(pageable))
                .thenReturn(new PageImpl<>(responses, pageable, 1));

        // When
        Page<ProductResponse> allProducts = productService.getAllProducts(pageable);

        // Then
        assertNotNull(allProducts);
        assertEquals(1, allProducts.getTotalElements());
        assertEquals(1, allProducts.getContent().size());
        assertEquals(productId, allProducts.getContent().get(0).getId());
        assertEquals("상품명", allProducts.getContent().get(0).getName());
        assertEquals(10000L, allProducts.getContent().get(0).getPrice());
    }
}