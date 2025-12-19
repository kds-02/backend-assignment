package com.codedrill.shoppingmall.point.service;

import com.codedrill.shoppingmall.point.entity.PointTxTest;
import com.codedrill.shoppingmall.point.repository.PointTxTestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * A 트랜잭션에서 호출하는, Propagation.NOT_SUPPORTED 로 동작하는 B 역할의 서비스.
 *
 * - 트랜잭션을 중단(suspend)하고, 별도 트랜잭션 없이 PointTxTest 레코드를 DB 에 직접 기록한다.
 * - 따라서 호출한 A 트랜잭션 입장에서는, 자신의 변경사항은 아직 커밋되지 않았더라도
 *   이 서비스가 만든 레코드는 바로 조회 가능하다.
 */
@Service
@RequiredArgsConstructor
public class PointTxTestService {

    private final PointTxTestRepository pointTxTestRepository;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void writeTestRecordWithoutTx(Long pointAccountId) {
        PointTxTest record = PointTxTest.builder()
                .pointAccountId(pointAccountId)
                .label("NOT_SUPPORTED_TEST")
                .build();

        pointTxTestRepository.save(record);
    }
}


