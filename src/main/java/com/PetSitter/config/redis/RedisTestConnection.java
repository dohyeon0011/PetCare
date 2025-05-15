package com.PetSitter.config.redis;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisTestConnection {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostConstruct
    public void testConnection() {
        try {
            redisTemplate.opsForValue().set("testKey", "value");
            String value = redisTemplate.opsForValue().get("testKey");
            System.out.println("Redis 연결 성공: " + value);
        } catch (Exception e) {
            System.err.println("Redis 연결 실패: " + e.getMessage());
        }
    }
}
