package com.plus.profile.product.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="PRODUCTS",
        indexes = {
            @Index(name = "IDX_PRODUCT_NAME", columnList = "PRODUCT_NAME"),
            @Index(name = "IDX_PRODUCT_PRICE", columnList = "PRODUCT_PRICE")
        })
@Getter
@Builder
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id", callSuper = false)
public class Product {
    @Id
    @Column(name="PRODUCT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="PRODUCT_NAME", nullable=false)
    private String name;
    @Column(name="PRODUCT_PRICE", nullable=false)
    private long price;

}
