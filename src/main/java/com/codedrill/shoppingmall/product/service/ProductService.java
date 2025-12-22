package com.codedrill.shoppingmall.product.service;

import com.codedrill.shoppingmall.common.entity.PrincipalDetails;
import com.codedrill.shoppingmall.common.exception.BusinessException;
import com.codedrill.shoppingmall.common.exception.ErrorCode;
import com.codedrill.shoppingmall.common.util.SecurityUtil;
import com.codedrill.shoppingmall.product.dto.*;
import com.codedrill.shoppingmall.product.entity.Product;
import com.codedrill.shoppingmall.common.enums.EnumProductStatus;
import com.codedrill.shoppingmall.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor

public class ProductService {
    private final ProductRepository productRepository;
    public ProductResponse createProduct(ProductCreateRequest request, PrincipalDetails user) {
        if (user == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        Product product = Product.builder()
                .status(EnumProductStatus.PENDING)
                .name(request.getName())
                .price(request.getPrice())
                .stock(request.getStock())
                .description(request.getDescription())
                .userId(user.getUserId())
                .build();

        Product savedProduct = productRepository.save(product);
        return toProductResponse(savedProduct);

    }

    private ProductResponse toProductResponse(Product savedProduct) {
        return ProductResponse.builder()
                .id(savedProduct.getId())
                .status(savedProduct.getStatus().name())
                .name(savedProduct.getName())
                .price(savedProduct.getPrice())
                .stock(savedProduct.getStock())
                .description(savedProduct.getDescription())
                .createdAt(savedProduct.getCreatedAt())
                .updatedAt(savedProduct.getUpdatedAt())
                .build();
    }

}

