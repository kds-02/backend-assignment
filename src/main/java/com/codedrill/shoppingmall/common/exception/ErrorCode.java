package com.codedrill.shoppingmall.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 공통
    VALIDATION_ERROR("VALIDATION_ERROR", "입력값이 올바르지 않습니다."),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다."),
    INVALID_REQUEST_BODY("INVALID_REQUEST_BODY", "요청 본문이 올바르지 않습니다."),
    METHOD_NOT_ALLOWED("METHOD_NOT_ALLOWED", "지원하지 않는 HTTP 메서드입니다."),
    NOT_FOUND("NOT_FOUND", "요청한 리소스를 찾을 수 없습니다."),
    
    // 인증/인가
    UNAUTHORIZED("UNAUTHORIZED", "인증이 필요합니다."),
    FORBIDDEN("FORBIDDEN", "권한이 없습니다."),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", "이메일 또는 비밀번호가 올바르지 않습니다."),
    DUPLICATE_EMAIL("DUPLICATE_EMAIL", "이미 존재하는 이메일입니다."),

    //유저
    USER_NOT_FOUND("USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),


    //상품
    PRODUCT_PENDING_EXISTS("PRODUCT_PENDING_EXISTS", "승인 대기 중인 상품이 이미 존재합니다.");


    private final String code;
    private final String message;
}

