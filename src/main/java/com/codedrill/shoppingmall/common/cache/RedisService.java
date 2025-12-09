package com.codedrill.shoppingmall.common.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 키-값 저장
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 키-값 저장 (TTL 설정)
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 키-값 저장 (Duration으로 TTL 설정)
     */
    public void set(String key, Object value, Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration);
    }

    /**
     * 값 조회
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 값 조회 (타입 지정)
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        return (T) value;
    }

    /**
     * 키 삭제
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 키 존재 여부 확인
     */
    public Boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * TTL 조회
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * TTL 설정
     */
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }
}

