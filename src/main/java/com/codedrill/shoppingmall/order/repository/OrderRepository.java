package com.codedrill.shoppingmall.order.repository;

import com.codedrill.shoppingmall.order.entity.Order;
import com.codedrill.shoppingmall.common.enums.EnumOrderStatus;
import com.codedrill.shoppingmall.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}

