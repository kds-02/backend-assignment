package com.codedrill.shoppingmall.product.service;

import com.codedrill.shoppingmall.common.entity.PrincipalDetails;
import com.codedrill.shoppingmall.common.enums.EnumRole;
import com.codedrill.shoppingmall.common.exception.BusinessException;
import com.codedrill.shoppingmall.common.exception.ErrorCode;
import com.codedrill.shoppingmall.common.util.SecurityUtil;
import com.codedrill.shoppingmall.product.dto.*;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.core.Authentication;
import com.codedrill.shoppingmall.common.enums.EnumProductStatus;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor

public class ProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    public ProductResponse createProduct(ProductCreateRequest request, PrincipalDetails user) {
        if (user == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        userRepository.findById(user.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));


        // PENDING 상태인 상품이 이미 있으면 새로운 상품 등록 불가
        long pendingCount = productRepository.countPendingProductsByUserId(user.getUserId(), EnumProductStatus.PENDING);
        if (pendingCount > 0) {
            throw new BusinessException(ErrorCode.PRODUCT_PENDING_EXISTS);
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

    public ProductPageResponse getProductList(Integer page, Integer size,
                                              Long minPrice, Long maxPrice, String name,
                                              PrincipalDetails principal) {

        int p = (page == null) ? 0 : page;
        int s = (size == null) ? 10 : size;
        Pageable pageable = PageRequest.of(p, s);

        boolean isAdmin = isAdmin(principal);
        Long requesterId = principal != null ? principal.getUserId() : null;

        Page<Product> result = isAdmin
                ? productRepository.findAllProductsForAdmin(minPrice, maxPrice, name, pageable)
                : requesterId != null
                ? productRepository.findApprovedOrOwnedProducts(
                EnumProductStatus.APPROVED, requesterId, minPrice, maxPrice, name, pageable)
                : productRepository.findApprovedProducts(
                EnumProductStatus.APPROVED, minPrice, maxPrice, name, pageable);
        return ProductPageResponse.builder()
                .content(result.getContent().stream()
                        .map(this::toProductSummary)
                        .toList())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .page(p)
                .size(s)
                .build();
    }

    private boolean isAdmin(PrincipalDetails principal) {
        if (principal != null) {
            return principal.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return false;

        return authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    private ProductSummary toProductSummary(Product p) {
        return ProductSummary.builder()
                .id(p.getId())
                .name(p.getName())
                .price(p.getPrice())
                .stock(p.getStock())
                .status(p.getStatus().name())
                .build();
    }

    @Transactional
    public ProductResponse approveProduct(Long productId, PrincipalDetails principal) {
        if (!isAdmin(principal)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        product.approve();
        return toProductResponse(product);
    }

    @Transactional(readOnly = true)
    public ProductDetailResponse getProduct(Long productId, PrincipalDetails principal) {
        boolean isAdmin = isAdmin(principal);
        Long requesterId = principal != null ? principal.getUserId() : null;

        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        if (product.getStatus() != EnumProductStatus.APPROVED) {
            boolean isOwner = requesterId != null && requesterId.equals(product.getUserId());
            if (!isAdmin && !isOwner) {
                throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
            }
        }

        return toProductDetailResponse(product);
    }

    private ProductDetailResponse toProductDetailResponse(Product product) {
        return ProductDetailResponse.builder()
                .id(product.getId())
                .status(product.getStatus().name())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .description(product.getDescription())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    @Transactional
    public ProductResponse updateProduct(Long productId, ProductUpdateRequest request, PrincipalDetails principal) {
        if (principal == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        boolean isAdmin = isAdmin(principal);
        boolean isOwner = principal.getUserId() != null && principal.getUserId().equals(product.getUserId());
        if (!isAdmin && !isOwner) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        if (request.getName() == null || request.getPrice() == null || request.getStock() == null) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "모든 필수 필드를 입력해주세요.");
        }

        product.update(request.getName(), request.getPrice(), request.getStock(), request.getDescription());
        return toProductResponse(product);
    }



}

