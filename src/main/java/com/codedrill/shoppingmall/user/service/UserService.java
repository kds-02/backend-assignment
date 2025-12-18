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
import com.codedrill.shoppingmall.user.dto.ReissueRequest;
import com.codedrill.shoppingmall.user.dto.ReissueResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;


    //암호화용
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public User signup(String email, String rawPassword, String name){

        //이메일 중복 확인
        if (userRepository.findByEmail(email).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        String encodedPassword = passwordEncoder.encode(rawPassword);

        User user = User.builder()
                .email(email)
                .password(encodedPassword)
                .name(name)
                .role(EnumRole.USER)
                .build();

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public String login(String email, String password) {

        //이메일 확인
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));

        //비밀번호 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        return jwtUtil.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole().name()
        );
    }


    @Transactional
    public ReissueResponse reissue(ReissueRequest request) {

        String refreshToken = request.getRefreshToken();

        // Refresh Token 유효성 검사 (만료 / 위조)
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        // Refresh Token에서 사용자 정보 추출
        Long userId = jwtUtil.extractUserId(refreshToken);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));

        // 새로운 Access Token 발급
        String newAccessToken = jwtUtil.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole().name()
        );

        // 응답 DTO 반환
        return new ReissueResponse(newAccessToken);
    }


}

