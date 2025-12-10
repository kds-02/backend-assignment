package com.codedrill.shoppingmall.common.exception;

import com.codedrill.shoppingmall.common.response.Response;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Response<Response.ErrorData>> handleBusinessException(BusinessException e) {
        log.warn("BusinessException: {}", e.getMessage());
        Response<Response.ErrorData> response = Response.error(
            e.getErrorCode().getCode(),
            e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<Response.ErrorData>> handleValidationException(
            MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        String message = errors.values().stream()
                .findFirst()
                .orElse("입력값이 올바르지 않습니다.");

        log.warn("ValidationException: {}", message);
        Response<Response.ErrorData> response = Response.error(
            ErrorCode.VALIDATION_ERROR.getCode(),
            message
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Response<Response.ErrorData>> handleBadCredentialsException(
            BadCredentialsException e) {
        log.warn("BadCredentialsException: {}", e.getMessage());
        Response<Response.ErrorData> response = Response.error(
            ErrorCode.INVALID_CREDENTIALS.getCode(),
            ErrorCode.INVALID_CREDENTIALS.getMessage()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Response<Response.ErrorData>> handleAccessDeniedException(
            AccessDeniedException e) {
        log.warn("AccessDeniedException: {}", e.getMessage());
        Response<Response.ErrorData> response = Response.error(
            ErrorCode.FORBIDDEN.getCode(),
            ErrorCode.FORBIDDEN.getMessage()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Response<Response.ErrorData>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e) {
        log.warn("HttpMessageNotReadableException: {}", e.getMessage());
        String message = "요청 본문을 읽을 수 없습니다. JSON 형식을 확인해주세요.";
        if (e.getMessage() != null && e.getMessage().contains("JSON")) {
            message = "잘못된 JSON 형식입니다.";
        }
        Response<Response.ErrorData> response = Response.error(
            ErrorCode.INVALID_REQUEST_BODY.getCode(),
            message
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Response<Response.ErrorData>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e) {
        log.warn("HttpRequestMethodNotSupportedException: {} method not supported", e.getMethod());
        String message = String.format("'%s' 메서드는 지원하지 않습니다.", e.getMethod());
        Response<Response.ErrorData> response = Response.error(
            ErrorCode.METHOD_NOT_ALLOWED.getCode(),
            message
        );
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Response<Response.ErrorData>> handleNoHandlerFoundException(
            NoHandlerFoundException e) {
        log.warn("NoHandlerFoundException: {} not found", e.getRequestURL());
        Response<Response.ErrorData> response = Response.error(
            ErrorCode.NOT_FOUND.getCode(),
            ErrorCode.NOT_FOUND.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Response<Response.ErrorData>> handleConstraintViolationException(
            ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        
        if (message.isEmpty()) {
            message = "입력값이 올바르지 않습니다.";
        }

        log.warn("ConstraintViolationException: {}", message);
        Response<Response.ErrorData> response = Response.error(
            ErrorCode.VALIDATION_ERROR.getCode(),
            message
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(JwtTokenInvalidException.class)
    public ResponseEntity<Response<Response.ErrorData>> handlerTokenNotValidateException(final JwtTokenInvalidException e) {
        Response<Response.ErrorData> response = Response.error(
                ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                ErrorCode.INTERNAL_SERVER_ERROR.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Response.ErrorData>> handleException(Exception e) {
        log.error("Unexpected error occurred", e);
        Response<Response.ErrorData> response = Response.error(
            ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
            ErrorCode.INTERNAL_SERVER_ERROR.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

