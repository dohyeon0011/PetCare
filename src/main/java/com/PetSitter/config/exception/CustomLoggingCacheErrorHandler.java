package com.PetSitter.config.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomLoggingCacheErrorHandler implements CacheErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(CustomLoggingCacheErrorHandler.class);

    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        log.error("Cache Get Error. key: {}, cache: {}", key, cache.getName(), exception);
    }

    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
        log.error("Cache Put Error. key: {}, cache: {}, value: {}", key, cache.getName(), value, exception);
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        log.error("Cache Evict Error. key: {}, cache: {}", key, cache.getName(), exception);
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        log.error("Cache Clear Error. cache: {}", cache.getName(), exception);
    }
}
