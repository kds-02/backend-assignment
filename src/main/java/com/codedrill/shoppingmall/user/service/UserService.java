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
    public UserResponse signup(SignupRequest request){

        //이메일 중복 체크
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL); // DUPLICATE_EMAIL 내용을 ErrorCode.java에 추가
        }

        //암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());


        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .name(request.getName())
                .role(EnumRole.USER)
                .build();

        User savedUser = userRepository.save(user);

        return UserResponse.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .name(savedUser.getName())
                .role(savedUser.getRole().name())
                .build();
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {

        //이메일 검증
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        // Access Token 생성
        String accessToken = jwtUtil.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole().name()
        );

        return new LoginResponse(accessToken);
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

