package com.codedrill.shoppingmall.user.controller;

import com.codedrill.shoppingmall.common.consts.RestUriConst;
import com.codedrill.shoppingmall.common.response.Response;
import com.codedrill.shoppingmall.user.dto.LoginRequest;
import com.codedrill.shoppingmall.user.dto.LoginResponse;
import com.codedrill.shoppingmall.user.dto.ReissueRequest;
import com.codedrill.shoppingmall.user.dto.ReissueResponse;
import com.codedrill.shoppingmall.user.dto.SignupRequest;
import com.codedrill.shoppingmall.user.dto.UserResponse;
import com.codedrill.shoppingmall.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(RestUriConst.REST_URI_AUTH)
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    @Operation(summary = "회원 가입")
    public Response<UserResponse> signup(@Valid @RequestBody SignupRequest request) {
        //TODO: 회원가입 구현

        UserResponse userResponse = userService.signup(request);

        return Response.success(userResponse);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인")
    public Response<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        //TODO: 로그인 구현
        LoginResponse loginResponse = userService.login(request);
        return Response.success(loginResponse);
    }

    @PostMapping("/reissue")
    @Operation(summary = "토큰 재발급")
    public Response<ReissueResponse> reissue(@Valid @RequestBody ReissueRequest request) {
        //TODO: 토큰 재발급 구현
        ReissueResponse reissueResponse = userService.reissue(request);
        return Response.success(reissueResponse);
    }
}

