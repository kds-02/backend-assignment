package com.codedrill.shoppingmall.order.controller;

import com.codedrill.shoppingmall.common.consts.RestUriConst;
import com.codedrill.shoppingmall.common.response.Response;
import com.codedrill.shoppingmall.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(RestUriConst.REST_URI_ORDER)
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "주문 목록 받아오기")
    public Response<Object> getOrderList() {
        //TODO: 주문 목록 받아오기 구현

        return Response.success();
    }
}
