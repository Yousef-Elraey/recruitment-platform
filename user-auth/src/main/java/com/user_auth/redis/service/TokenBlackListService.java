package com.user_auth.redis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenBlackListService {

    private final RedisTemplate<String, String> redisTemplate;

    public void blacklistToken(
            String token,
            long ttlMillis) {

        redisTemplate.opsForValue()
                .set(
                        token,
                        "BLACKLISTED",
                        Duration.ofMillis(ttlMillis)
                );

    }

    public boolean isBlacklisted(String token) {

        return Boolean.TRUE.equals(
                redisTemplate.hasKey(token));
    }
}
