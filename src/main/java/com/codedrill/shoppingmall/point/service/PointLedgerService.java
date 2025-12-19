package com.codedrill.shoppingmall.point.service;

import com.codedrill.shoppingmall.point.entity.PointAccount;
import com.codedrill.shoppingmall.point.entity.PointLedger;
import com.codedrill.shoppingmall.point.repository.PointLedgerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointLedgerService {

    private final PointLedgerRepository pointLedgerRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void writeAccountCreatedLedgerWithNewTx(Long pointAccountId) {
        // 조회 없이 ID 만 가진 PointAccount 프록시 오브젝트 구성
        PointAccount accountRef = PointAccount.builder()
                .id(pointAccountId)
                .build();

        PointLedger ledger = PointLedger.builder()
                .pointAccount(accountRef)
                .amount(0L)
                .type("ACCOUNT_CREATED")
                .build();

        pointLedgerRepository.save(ledger);
    }
}



