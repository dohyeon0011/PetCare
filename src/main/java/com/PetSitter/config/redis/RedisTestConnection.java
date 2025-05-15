package com.PetSitter.config.redis;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RedisTestConnection {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostConstruct
    public void testConnection() {
        try {
            redisTemplate.opsForValue().set("testKey", "value");
            String value = redisTemplate.opsForValue().get("testKey");
            log.info("Redis 연결 성공: {}", value);
        } catch (Exception e) {
            log.error("Redis 연결 실패: ", e);
        }
    }
}
