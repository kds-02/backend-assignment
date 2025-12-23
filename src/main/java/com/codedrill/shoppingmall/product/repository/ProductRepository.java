package com.codedrill.shoppingmall.product.repository;

import com.codedrill.shoppingmall.common.enums.EnumProductStatus;
import com.codedrill.shoppingmall.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
        SELECT COUNT(p)
        FROM Product p
        WHERE p.userId = :userId
          AND p.status = :status
          AND p.deletedAt IS NULL
    """)
    long countPendingProductsByUserId(
            @Param("userId") Long userId,
            @Param("status") EnumProductStatus status
    );
}

