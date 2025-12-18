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
import com.codedrill.shoppingmall.user.entity.User;

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

        User user = userService.signup(
                request.getEmail(),
                request.getPassword(),
                request.getName()
        );

        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .build();

        return Response.success(userResponse);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인")
    public Response<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        String accessToken = userService.login(
                request.getEmail(),
                request.getPassword()
        );

        return Response.success(new LoginResponse(accessToken));
    }

    @PostMapping("/reissue")
    @Operation(summary = "토큰 재발급")
    public Response<ReissueResponse> reissue(@Valid @RequestBody ReissueRequest request) {
        //TODO: 토큰 재발급 구현
        ReissueResponse reissueResponse = userService.reissue(request);
        return Response.success(reissueResponse);
    }
}

