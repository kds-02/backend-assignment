package com.codedrill.shoppingmall.common.exception;

public class JwtTokenInvalidException extends BusinessException {
    // 자주 발생할 수 있는 Exception 이기 때문에 Singleton 화
    public static final JwtTokenInvalidException INSTANCE = new JwtTokenInvalidException();

    private JwtTokenInvalidException() {
        super(ErrorCode.UNAUTHORIZED);
    }

}