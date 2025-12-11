package com.codedrill.shoppingmall.user.controller;

import com.codedrill.shoppingmall.common.consts.RestUriConst;
import com.codedrill.shoppingmall.common.response.Response;
import com.codedrill.shoppingmall.user.dto.UserResponse;
import com.codedrill.shoppingmall.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(RestUriConst.REST_URI_USER)
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "자신의 정보 받아오기")
    public Response<UserResponse> getMyInfo() {
        //TODO: 자기 자신의 정보 받아오기 구현

        return Response.success();
    }
}

