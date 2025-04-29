package com.plus.profile.product.presentation.dto;


import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class ProductResponse{
    private final Long id;
    private final String name;
    private final long price;
    @QueryProjection
    public ProductResponse(Long id, String name, long price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
}
