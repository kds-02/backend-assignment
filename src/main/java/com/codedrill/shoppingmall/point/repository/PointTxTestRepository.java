package com.codedrill.shoppingmall.point.repository;

import com.codedrill.shoppingmall.point.entity.PointTxTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointTxTestRepository extends JpaRepository<PointTxTest, Long> {

    List<PointTxTest> findByPointAccountId(Long pointAccountId);
}


