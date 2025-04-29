package com.plus.profile.product.infra.impl;

import com.plus.profile.product.domain.Product;
import com.plus.profile.product.domain.repository.ProductRepository;
import com.plus.profile.product.infra.ProductRepositoryCustom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
class ProductRepositoryCustomImplTest {
    @Autowired
    private ProductRepositoryCustom productRepositoryCustom;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Product product = Product.builder()
                    .name("상품명"+i)
                    .price(10000L)
                    .build();
            products.add(product);
        }
        productRepository.saveAll(products);
    }
    @Test
    void findAllProducts() {
        // Given
        int page = 0;
        int size = 10;

        // When
        var result = productRepositoryCustom.findAllProducts(PageRequest.of(page, size));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(20);
        assertThat(result.getContent().size()).isEqualTo(10);
    }
}