package com.codedrill.shoppingmall.common.util;

import com.codedrill.shoppingmall.common.exception.JwtTokenInvalidException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.secret}")
    String jwtSecretStr;

    // AccessToken 만료 시간
    public static Long AC_EXPIRATION_IN_MS = 3600000L;

    // RefreshToken 만료 시간
    public static Long RF_EXPIRATION_IN_MS = 604800000L;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecretStr.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(Long userId, String email, String name, String role) {
        //TODO: accessToken 생성 로직 구현
        Date now = new Date();
        Date expiration = new Date(now.getTime() + AC_EXPIRATION_IN_MS);

        return Jwts.builder()
                // 토큰 주체: 사용자 식별자
                .subject(String.valueOf(userId))

                // AccessToken에 담을 정보
                .claim("email", email)
                .claim("name", name)
                .claim("role", role)

                // 발급 시간 / 만료 시간
                .issuedAt(now)
                .expiration(expiration)

                // 서명
                .signWith(getSigningKey())

                // JWT 문자열 생성
                .compact();
    }

    public String generateRefreshToken(Long userId) {
        //TODO: refreshToken 생성 로직 구현
        Date now = new Date();
        Date expiration = new Date(now.getTime() + RF_EXPIRATION_IN_MS);
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long extractUserId(String token) {
        Claims claims = extractClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    public String extractUserName(String token) {
        Claims claims = extractClaims(token);
        return claims.get("name", String.class);
    }

    public String extractEmail(String token) {
        Claims claims = extractClaims(token);
        return claims.get("email", String.class);
    }

    public String extractRole(String token) {
        Claims claims = extractClaims(token);
        return claims.get("role", String.class);
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            throw JwtTokenInvalidException.INSTANCE;
        }
    }

    public boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            throw JwtTokenInvalidException.INSTANCE;
        }
    }
}

