package com.codedrill.shoppingmall.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {
    private String status;
    private T data;

    public static <T> Response<T> success(T data) {
        return new Response<>("SUCCESS", data);
    }

    public static <T> Response<T> success() {
        return new Response<>("SUCCESS", null);
    }

    public static <T> Response<T> error(String code, String message) {
        ErrorData errorData = new ErrorData(code, message);
        return new Response<>("ERROR", (T) errorData);
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorData {
        private String code;
        private String message;
    }
}

