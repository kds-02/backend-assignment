package com.codedrill.shoppingmall.point.service;

import com.codedrill.shoppingmall.common.exception.BusinessException;
import com.codedrill.shoppingmall.common.exception.ErrorCode;
import com.codedrill.shoppingmall.point.entity.PointAccount;
import com.codedrill.shoppingmall.point.repository.PointAccountRepository;
import com.codedrill.shoppingmall.point.repository.PointLedgerRepository;
import com.codedrill.shoppingmall.point.repository.PointTxTestRepository;
import com.codedrill.shoppingmall.user.entity.User;
import com.codedrill.shoppingmall.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointAccountRepository pointAccountRepository;
    private final UserRepository userRepository;
    private final PointLedgerService pointLedgerService;
    private final PointTxTestService pointTxTestService;
    private final PointTxTestRepository pointTxTestRepository;

    @Transactional
    public void initAccountWithLedger(Long userId) {
        PointAccount account = getOrCreateAccount(userId);

        pointLedgerService.writeAccountCreatedLedgerWithNewTx(account.getId());
    }

    @Transactional
    public int demoNotSupportedWithTestEntity(Long userId) {
        PointAccount account = getOrCreateAccount(userId);

        pointTxTestService.writeTestRecordWithoutTx(account.getId());

        return pointTxTestRepository.findByPointAccountId(account.getId()).size();
    }

    private PointAccount getOrCreateAccount(Long userId) {
        return pointAccountRepository.findByUserIdForUpdate(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "User not found"));

                    PointAccount newAccount = PointAccount.builder()
                            .user(user)
                            .balance(0L)
                            .build();
                    return pointAccountRepository.save(newAccount);
                });
    }
}


