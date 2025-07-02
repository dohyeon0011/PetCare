package com.PetSitter.config.redis;

import com.PetSitter.config.exception.CustomLoggingCacheErrorHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * @Cacheable(스프링이 제공하는 추상화된 캐시 기능) 사용 시 CacheManager에서 RedisCacheManager 설정하기(얘는 Cache Aside 읽기 전략을 씀)
 * CacheManager -> RedisConnectionFactory로 Redis에 저장(RedisCacheManager를 통해서 Redis와 연결하고 동작)
 */
@Configuration
@EnableCaching
public class SpringRedisCacheableConfig {

    @Bean
    public CacheErrorHandler cacheErrorHandler() {
        return new CustomLoggingCacheErrorHandler();
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // LocalDateTime 직렬화 문제 -> jackson-datatype-jsr310 모듈 등록 필수
        // Redis 캐시용 GenericJackson2JsonRedisSerializer를 생성할 때 ObjectMapper에 JavaTimeModule 등록 후 주입
        // 그러면 LocalDateTime 포함된 DTO도 Redis 캐시에 문제 없이 저장됨
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(mapper);

        // 전역 설정
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(2))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

        // 캐시 이름별 TTL 설정하기
        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
        configMap.put("petHospitalList", RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(12)) // 동물 병원 리스트 데이터 -> 12시간 유지
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer)));

        configMap.put("petHospitalListCount", RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(12)) // 동물 병원 리스트 카운트 쿼리 -> 12시간 유지
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)   // 아무 설정도 없는 캐시 이름이 사용될 때
                .withInitialCacheConfigurations(configMap)  // 특정 캐시 이름별 개별 설정 등록(cacheDefaults보다 우선순위 높음)
                .build();
    }
}
