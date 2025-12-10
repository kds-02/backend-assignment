package com.codedrill.shoppingmall.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import static com.codedrill.shoppingmall.common.config.JwtAuthenticationFilter.ATTRIBUTE_TOKEN_ERROR;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final HandlerExceptionResolver resolver;

    public CustomAuthenticationEntryPoint(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        Exception exception = (Exception) request.getAttribute(ATTRIBUTE_TOKEN_ERROR);
        
        if (exception != null) {
            if (exception instanceof JwtTokenInvalidException jwtTokenInvalidException) {
                resolver.resolveException(request, response, null, jwtTokenInvalidException);
            } else {
                // 처리되지 않은 exception이 발생한 경우입니다.
                log.error("{}: {}", exception.getClass(), exception.getMessage());
                resolver.resolveException(request, response, null, exception);
            }
            return;
        }

        // 토큰 에러가 없는 경우 (인증이 필요한데 토큰이 없거나 유효하지 않은 경우)
        // BadCredentialsException을 던져서 GlobalExceptionHandler가 처리하도록 함
        if (response.isCommitted()) {
            log.warn("응답이 이미 커밋되었습니다. URI: {}", request.getRequestURI());
            return;
        }

        org.springframework.security.authentication.BadCredentialsException badCredentialsException = 
            new org.springframework.security.authentication.BadCredentialsException("인증이 필요합니다.");
        resolver.resolveException(request, response, null, badCredentialsException);
    }

}
