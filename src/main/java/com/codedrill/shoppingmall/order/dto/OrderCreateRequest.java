package com.codedrill.shoppingmall.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OrderCreateRequest {

    @Valid
    @NotEmpty(message = "최소 1개 이상의 주문 항목이 필요합니다.")
    private List<OrderItemRequest> items;
}

