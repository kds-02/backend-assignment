package com.codedrill.shoppingmall.user.service;

import com.codedrill.shoppingmall.common.enums.EnumRole;
import com.codedrill.shoppingmall.common.exception.BusinessException;
import com.codedrill.shoppingmall.common.exception.ErrorCode;
import com.codedrill.shoppingmall.common.util.JwtUtil;
import com.codedrill.shoppingmall.user.dto.LoginRequest;
import com.codedrill.shoppingmall.user.dto.LoginResponse;
import com.codedrill.shoppingmall.user.dto.SignupRequest;
import com.codedrill.shoppingmall.user.dto.UserResponse;
import com.codedrill.shoppingmall.user.entity.User;
import com.codedrill.shoppingmall.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

}

