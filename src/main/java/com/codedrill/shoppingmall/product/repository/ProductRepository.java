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

    @Query("""
        SELECT p
        FROM Product p
        WHERE p.deletedAt IS NULL
          AND p.status = :status
          AND (:minPrice IS NULL OR p.price >= :minPrice)
          AND (:maxPrice IS NULL OR p.price <= :maxPrice)
          AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
    """)
    Page<Product> findApprovedProducts(
            @Param("status") EnumProductStatus status,
            @Param("minPrice") Long minPrice,
            @Param("maxPrice") Long maxPrice,
            @Param("name") String name,
            Pageable pageable
    );


    @Query("""
        SELECT p
        FROM Product p
        WHERE p.deletedAt IS NULL
          AND (:minPrice IS NULL OR p.price >= :minPrice)
          AND (:maxPrice IS NULL OR p.price <= :maxPrice)
          AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
    """)
    Page<Product> findAllProductsForAdmin(
            @Param("minPrice") Long minPrice,
            @Param("maxPrice") Long maxPrice,
            @Param("name") String name,
            Pageable pageable
    );

    Optional<Product> findByIdAndDeletedAtIsNull(Long id);


}

