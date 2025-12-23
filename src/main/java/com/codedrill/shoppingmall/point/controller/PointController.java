package com.codedrill.shoppingmall.point.controller;

import com.codedrill.shoppingmall.common.consts.RestUriConst;
import com.codedrill.shoppingmall.common.entity.PrincipalDetails;
import com.codedrill.shoppingmall.common.response.Response;
import com.codedrill.shoppingmall.point.service.PointService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 포인트 계좌 생성 관련 API.
 *
 * 요구사항:
 * - "포인트 계좌를 생성할 때, 그 사실을 Ledger 에 남기는 과정에서 트랜잭션 문제가 발생한다"
 *   는 시나리오를 실습하기 위한 엔드포인트이다.
 * - 여러 클라이언트/스레드가 동시에 같은 사용자에 대해 이 API 를 호출했을 때
 *   데드락/Lock wait timeout 이 발생할 수 있도록 서비스 레이어가 설계되어 있다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(RestUriConst.REST_URI_POINT)
public class PointController {

    private final PointService pointService;

    @PostMapping("/accounts/init")
    @Operation(summary = "포인트 계좌 생성 + Ledger 기록 (데드락 실습용)")
    public Response<Void> initPointAccount(
            @AuthenticationPrincipal PrincipalDetails principal
    ) {
        Long userId = principal.getUserId();

        pointService.initAccountWithLedger(userId);
        return Response.success();
    }

    @PostMapping("/tx/not-supported-test-entity")
    @Operation(summary = "PointTxTest 엔티티로 A -> B(NOT_SUPPORTED) -> A 조회 데모")
    public Response<Integer> demoNotSupportedWithTestEntity(
            @AuthenticationPrincipal PrincipalDetails principal
    ) {
        Long userId = principal.getUserId();
        int count = pointService.demoNotSupportedWithTestEntity(userId);
        return Response.success(count);
    }
}


