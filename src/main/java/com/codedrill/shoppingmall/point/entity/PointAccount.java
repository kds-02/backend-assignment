package com.codedrill.shoppingmall.point.entity;

import com.codedrill.shoppingmall.common.entity.BaseEntity;
import com.codedrill.shoppingmall.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "point_account")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PointAccount extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private Long balance;

    public void charge(Long amount) {
        if (balance == null) {
            balance = 0L;
        }
        balance += amount;
    }

    public void use(Long amount) {
        if (balance == null) {
            balance = 0L;
        }
        balance -= amount;
    }
}


