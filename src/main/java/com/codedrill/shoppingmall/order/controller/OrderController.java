package com.codedrill.shoppingmall.order.controller;

import com.codedrill.shoppingmall.common.consts.RestUriConst;
import com.codedrill.shoppingmall.common.entity.PrincipalDetails;
import com.codedrill.shoppingmall.common.exception.BusinessException;
import com.codedrill.shoppingmall.common.exception.ErrorCode;
import com.codedrill.shoppingmall.common.response.Response;
import com.codedrill.shoppingmall.order.dto.OrderCreateRequest;
import com.codedrill.shoppingmall.order.dto.OrderDetailResponse;
import com.codedrill.shoppingmall.order.dto.OrderResponse;
import com.codedrill.shoppingmall.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(RestUriConst.REST_URI_ORDER)
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "주문 생성")
    public Response<OrderResponse> createOrder(
            @AuthenticationPrincipal PrincipalDetails principal,
            @Valid @RequestBody OrderCreateRequest request
    ) {
        //TODO: 주문 생성 구현
        Long userId = principal.getUserId();
        OrderResponse response = orderService.createOrder(userId, request);
        return Response.success(response);
    }

    @GetMapping("/my")
    @Operation(summary = "내 주문 목록 조회")
    public Response<Page<OrderResponse>> getMyOrders(@RequestParam(required = false, defaultValue = "0") Integer page,
                                                     @RequestParam(required = false, defaultValue = "10") Integer size,
                                                     @AuthenticationPrincipal PrincipalDetails principal) {
        //TODO: 내 주문 목록 조회 구현
        if (principal == null || principal.getUserId() == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        Page<OrderResponse> response = orderService.getMyOrders(principal.getUserId(), page, size);
        return Response.success(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "주문 상세 조회")
    public Response<OrderDetailResponse> getOrder(@PathVariable Long id,
                                                  @AuthenticationPrincipal PrincipalDetails principal) {
        //TODO: 주문 상세 조회 구현

        OrderDetailResponse order = orderService.getOrder(id, principal);
        return Response.success(order);
    }

    @PatchMapping("/{id}/pay")
    @Operation(summary = "주문 결제")
    public Response<OrderResponse> payOrder(@PathVariable Long id,
                                            @AuthenticationPrincipal Object principal) {
        //TODO: 주문 결제 구현
        OrderResponse response = orderService.payOrder(id, principal);
        return Response.success(response);
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "주문 취소")
    public Response<OrderResponse> cancelOrder(@PathVariable Long id,
                                               @AuthenticationPrincipal Object principal) {
        //TODO: 주문 취소 구현
        OrderResponse response = orderService.cancelOrder(id, principal);

        return Response.success(response);
    }

    @PatchMapping("/{id}/complete")
    @Operation(summary = "주문 완료")
    public Response<OrderResponse> completeOrder(@PathVariable Long id,
                                                 @AuthenticationPrincipal Object principal){
        //TODO: 주문 완료 구현
        OrderResponse response = orderService.completeOrder(id, principal);
        return Response.success(response);
    }

}
