package com.codedrill.shoppingmall.order.entity;

import com.codedrill.shoppingmall.common.entity.BaseEntity;
import com.codedrill.shoppingmall.common.enums.EnumOrderStatus;
import com.codedrill.shoppingmall.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "orders")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnumOrderStatus status;

    @Column(nullable = false)
    private Long totalPrice;

    public void changeStatus(EnumOrderStatus status) {
        this.status = status;
    }
}

