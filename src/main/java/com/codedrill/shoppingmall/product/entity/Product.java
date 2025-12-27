package com.codedrill.shoppingmall.product.entity;

import com.codedrill.shoppingmall.common.entity.BaseEntity;
import com.codedrill.shoppingmall.common.enums.EnumProductStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "products")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SQLDelete(sql = "UPDATE products SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnumProductStatus status;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private Integer stock;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Long userId;


    public void approve() {
        this.status = EnumProductStatus.APPROVED;
    }

    public void update(String name, Long price, Integer stock, String description) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.description = description;
    }

    public void decreaseStock(Integer quantity) {
        this.stock -= quantity;
    }
    public void increaseStock(Integer quantity) {
        this.stock += quantity;
    }
}

