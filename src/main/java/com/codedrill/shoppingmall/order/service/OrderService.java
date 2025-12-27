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
import org.springframework.security.core.userdetails.UserDetails;
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

    @Transactional
    public Page<OrderResponse> getMyOrders(Long userId, Integer page, Integer size) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        int p = (page == null) ? 0 : page;
        int s = (size == null) ? 10 : size;
        Pageable pageable = PageRequest.of(p, s);

        Page<Order> orders = orderRepository.findByUserIdAndDeletedAtIsNull(userId, pageable);
        return orders.map(this::toOrderResponse);

    }

    public OrderDetailResponse getOrder(Long id, PrincipalDetails user) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        // 권한 체크: 본인 주문이거나 ADMIN만 조회 가능
        if (!SecurityUtil.isAdmin(user) && !order.getUser().getId().equals(user.getUserId())) {
            throw new BusinessException(ErrorCode.ORDER_ACCESS_DENIED);
        }

        // OrderItem 조회
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(id);

        return toOrderDetailResponse(order, orderItems);
    }

    private OrderDetailResponse toOrderDetailResponse(Order order, List<OrderItem> items) {
        List<OrderItemResponse> itemDtos = items.stream()
                .map(i -> OrderItemResponse.builder()
                        .id(i.getId())
                        .productId(i.getProduct().getId())
                        .productName(i.getProduct().getName())   // 상품 엔티티의 필드명에 맞게 조정
                        .price(i.getPrice())
                        .quantity(i.getQuantity())
                        .build())
                .toList();

        return OrderDetailResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .status(order.getStatus().name())
                .totalPrice(order.getTotalPrice())
                .items(itemDtos)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }


    @Transactional
    public OrderResponse payOrder(Long id, Object principal) {
        Order order = getActiveOrder(id);
        validateAccess(order, principal, false);

        if (order.getStatus() != EnumOrderStatus.CREATED) {
            throw new BusinessException(ErrorCode.INVALID_ORDER_STATUS, "결제할 수 없는 주문 상태입니다.");
        }

        order.changeStatus(EnumOrderStatus.PAID);
        return toOrderResponse(order);
    }

    private Order getActiveOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        if (order.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }

        return order;
    }

    private void validateAccess(Order order, Object principal, boolean adminOnly) {
        if (principal == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        boolean isAdmin = isAdmin(principal);
        if (adminOnly && !isAdmin) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        Long requesterId = extractUserId(principal);
        if (!isAdmin && requesterId != null && !order.getUser().getId().equals(requesterId)) {
            throw new BusinessException(ErrorCode.ORDER_ACCESS_DENIED);
        }
    }

    private boolean isAdmin(Object principal) {
        if (principal instanceof PrincipalDetails principalDetails) {
            return SecurityUtil.isAdmin(principalDetails);
        }

        if (principal instanceof UserDetails userDetails) {
            return userDetails.getAuthorities().stream()
                    .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));
        }

        return false;
    }


    private Long extractUserId(Object principal) {
        if (principal instanceof PrincipalDetails principalDetails) {
            return principalDetails.getUserId();
        }

        return null;
    }
}
