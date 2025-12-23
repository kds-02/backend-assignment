package com.codedrill.shoppingmall.point.repository;

import com.codedrill.shoppingmall.point.entity.PointAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface PointAccountRepository extends JpaRepository<PointAccount, Long> {

    Optional<PointAccount> findByUserId(Long userId);

    @Query("select p from PointAccount p where p.user.id = :userId")
    Optional<PointAccount> findByUserIdForUpdate(@Param("userId") Long userId);
}


