package com.plus.profile.product.domain.repository;

import com.plus.profile.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
