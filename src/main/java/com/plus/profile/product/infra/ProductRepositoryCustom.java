package com.plus.profile.product.infra;

import com.plus.profile.product.presentation.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {
    Page<ProductResponse> findAllProducts(Pageable pageable);
}
