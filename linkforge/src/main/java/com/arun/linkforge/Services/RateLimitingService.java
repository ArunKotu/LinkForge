package com.arun.linkforge.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimitingService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final long WINDOW_SIZE_MS = 60_000; // 10 seconds
    private static final int MAX_REQUESTS = 10;

    @Autowired
    public RateLimitingService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isAllowed(String userId) {
        String key = "rate_limit:" + userId;
        long now = System.currentTimeMillis();
        long windowStart = now - WINDOW_SIZE_MS;

        redisTemplate.opsForZSet().removeRangeByScore(key, 0, windowStart);

        Long count = redisTemplate.opsForZSet().zCard(key);

        if (count != null && count >= MAX_REQUESTS) {
            return false; // Block request
        }

        redisTemplate.opsForZSet().add(key, String.valueOf(now), now);

        redisTemplate.expire(key, Duration.ofSeconds(10));

        return true;
    }
}
