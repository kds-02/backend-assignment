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

import com.codedrill.shoppingmall.common.entity.PrincipalDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.GrantedAuthority;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(RestUriConst.REST_URI_USER)
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "자신의 정보 받아오기")
    public Response<UserResponse> getMyInfo(@AuthenticationPrincipal PrincipalDetails principal) {
        //TODO: 자기 자신의 정보 받아오기 구현
        String role = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);


        UserResponse userResponse = UserResponse.builder()
                .id(principal.getUserId())
                .email(principal.getEmail())
                .name(principal.getUsername())
                .role(role)
                .build();


        return Response.success(userResponse);
    }
}

