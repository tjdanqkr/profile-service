package com.plus.profile.product.application.impl;

import com.plus.profile.product.application.ProductService;
import com.plus.profile.product.infra.ProductRepositoryCustom;
import com.plus.profile.product.presentation.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepositoryCustom productRepositoryCustom;

    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepositoryCustom.findAllProducts(pageable);
    }
}
