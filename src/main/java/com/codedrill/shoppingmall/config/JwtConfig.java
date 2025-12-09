package com.codedrill.shoppingmall.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    private String secret = "your-secret-key-change-this-in-production-environment-minimum-256-bits";
    private Long accessTokenExpiration = 3600000L; // 1시간 (밀리초)
    private Long refreshTokenExpiration = 604800000L; // 7일 (밀리초)
}

