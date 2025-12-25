package com.codedrill.shoppingmall.order.service;

import com.codedrill.shoppingmall.common.entity.PrincipalDetails;
import com.codedrill.shoppingmall.common.exception.BusinessException;
import com.codedrill.shoppingmall.common.exception.ErrorCode;
import com.codedrill.shoppingmall.common.util.SecurityUtil;
import com.codedrill.shoppingmall.order.dto.*;
import com.codedrill.shoppingmall.order.entity.Order;
import com.codedrill.shoppingmall.order.entity.OrderItem;
import com.codedrill.shoppingmall.common.enums.EnumOrderStatus;
import com.codedrill.shoppingmall.order.repository.OrderItemRepository;
import com.codedrill.shoppingmall.order.repository.OrderRepository;
import com.codedrill.shoppingmall.product.entity.Product;
import com.codedrill.shoppingmall.common.enums.EnumProductStatus;
import com.codedrill.shoppingmall.product.repository.ProductRepository;
import com.codedrill.shoppingmall.user.entity.User;
import com.codedrill.shoppingmall.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    private final UserRepository userRepository;
    private final ProductRepository productRepository;


    // Order 전용 임시 데이터
    private static class OrderItemData {
        private final Product product;
        private final Long price;
        private final Integer quantity;

        public OrderItemData(Product product, Long price, Integer quantity) {
            this.product = product;
            this.price = price;
            this.quantity = quantity;
        }

        public Product getProduct() {
            return product;
        }

        public Long getPrice() {
            return price;
        }

        public Integer getQuantity() {
            return quantity;
        }
    }


    @Transactional
    public OrderResponse createOrder(Long userId, OrderCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        List<OrderItemData> itemDataList = request.getItems().stream()
                .map(itemRequest -> {
                    Product product = productRepository.findByIdAndDeletedAtIsNull(itemRequest.getProductId())
                            .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

                    // 승인된 상품만 주문 가능
                    if (product.getStatus() != EnumProductStatus.APPROVED) {
                        throw new BusinessException(ErrorCode.PRODUCT_NOT_APPROVED);
                    }

                    // 재고 검증
                    if (product.getStock() < itemRequest.getQuantity()) {
                        throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK);
                    }

                    // 재고 차감
                    product.decreaseStock(itemRequest.getQuantity());

                    return new OrderItemData(product, product.getPrice(), itemRequest.getQuantity());
                })
                .collect(Collectors.toList());

        // 총 가격 계산
        Long totalPrice = itemDataList.stream()
                .mapToLong(item -> item.getPrice() * item.getQuantity())
                .sum();

        // 주문 생성
        Order order = Order.builder()
                .user(user)
                .status(EnumOrderStatus.CREATED)
                .totalPrice(totalPrice)
                .build();

        Order savedOrder = orderRepository.save(order);

        // OrderItem 생성 및 저장
        List<OrderItem> orderItems = itemDataList.stream()
                .map(itemData -> {
                    OrderItem orderItem = OrderItem.builder()
                            .order(savedOrder)
                            .product(itemData.getProduct())
                            .price(itemData.getPrice())
                            .quantity(itemData.getQuantity())
                            .build();
                    return orderItemRepository.save(orderItem);
                })
                .collect(Collectors.toList());

        return toOrderResponse(savedOrder);

    }


    private OrderResponse toOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .status(order.getStatus().name())
                .totalPrice(order.getTotalPrice())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

}
