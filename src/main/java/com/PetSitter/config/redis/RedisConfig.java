package com.PetSitter.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

// Redis 기반 챗봇 AI 설정 Config 클래스
@Configuration
public class RedisConfig {
    // jedis: cpu 효율성 측면에서 별로 좋지 않음. 극한으로 끌어 올리려면 적절한 커넥션 풀을 찾아서 정해야 함.(동기적으로 처리)
    // Lettuce: 비동기로 처리하기 때문에 성능적인 측면에서 좋음.
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {    // RedisConnectionFactory: Redis 데이터베이스와의 연결을 설정하는 역할
        return new LettuceConnectionFactory(); // 기본 host: localhost, port: 6379
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, String> template = new RedisTemplate<>(); // RedisTemplate: Redis 데이터베이스와 상호작용하기 위한 설정을 담은 클래스
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());

        return template;
    }
}

