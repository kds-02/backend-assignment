package com.codedrill.shoppingmall.point.repository;

import com.codedrill.shoppingmall.point.entity.PointLedger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointLedgerRepository extends JpaRepository<PointLedger, Long> {

    List<PointLedger> findByPointAccountId(Long pointAccountId);
}


