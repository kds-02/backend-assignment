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



    @Transactional
    public OrderResponse createOrder(Long userId, OrderCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        List<Product> orderedProducts = new ArrayList<>();
        List<Integer> quantities = new ArrayList<>();
        long totalPrice = 0L;

        // 승인된 상품만 주문 가능
        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findByIdAndDeletedAtIsNull(itemRequest.getProductId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

            // 재고 검증
            if (product.getStatus() != EnumProductStatus.APPROVED) {
                throw new BusinessException(ErrorCode.PRODUCT_NOT_APPROVED);
            }

            // 재고 차감
            if (product.getStock() < itemRequest.getQuantity()) {
                throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK);
            }

            product.decreaseStock(itemRequest.getQuantity());

        // 총 가격 계산
            orderedProducts.add(product);
            quantities.add(itemRequest.getQuantity());
            totalPrice += product.getPrice() * itemRequest.getQuantity();
        }

        // 주문 생성
        Order order = Order.builder()
                .user(user)
                .status(EnumOrderStatus.CREATED)
                .totalPrice(totalPrice)
                .build();

        Order savedOrder = orderRepository.save(order);

        // OrderItem 생성 및 저장
        for (int i = 0; i < orderedProducts.size(); i++) {
            OrderItem orderItem = OrderItem.builder()
                    .order(savedOrder)
                    .product(orderedProducts.get(i))
                    .price(orderedProducts.get(i).getPrice())
                    .quantity(quantities.get(i))
                    .build();
            orderItemRepository.save(orderItem);
        }

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
