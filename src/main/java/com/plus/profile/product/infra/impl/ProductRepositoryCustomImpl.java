package com.plus.profile.product.infra.impl;

import com.plus.profile.product.infra.ProductRepositoryCustom;
import com.plus.profile.product.presentation.dto.ProductResponse;
import com.plus.profile.product.presentation.dto.QProductResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.plus.profile.product.domain.QProduct.product;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ProductResponse> findAllProducts(Pageable pageable) {
        List<ProductResponse> products = queryFactory
                .select(new QProductResponse(product.id, product.name, product.price))
                .from(product)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        Long total = queryFactory
                .select(product.count())
                .from(product)
                .fetchOne();
        return new PageImpl<>(products, pageable, total==null ? 0 : total);

    }
}
