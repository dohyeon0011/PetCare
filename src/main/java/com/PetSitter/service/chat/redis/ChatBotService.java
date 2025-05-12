package com.PetSitter.service.chat.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatBotService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 게스트 채팅 기록 저장
    public void saveChatMessageForGuest(String uuid, String message) {
        String key = "chat:guest:" + uuid;
        log.info("saveChatMessageForGuest() 호출: [key=" + key +", save message=" + message + "]");
        redisTemplate.opsForList().rightPush(key, message);
        redisTemplate.expire(key, Duration.ofHours(6));
    }

    // 로그인 유저 채팅 기록 저장
    public void saveChatMessageForUser(Long userId, String message) {
        String key = "chat:user:" + userId;
        log.info("saveChatMessageForUser() 호출:[key=" + key +", save message=" + message + "]");
        redisTemplate.opsForList().rightPush(key, message);
    }

    // 게스트 -> 로그인 유저 채팅 기록 마이그레이션
    public void migrateGuestChatToUser(String guestUUID, Long userId) {
        String guestKey = "chat:guest:" + guestUUID;
        String userKey = "chat:user:" + userId;
        log.info("migrateGuestChatToUser 호출: [guestKey=" + guestKey +", userKey=" + userKey + "]");

        List<String> guestMessages = redisTemplate.opsForList().range(guestKey, 0, -1);
        if (guestMessages != null) {
            for (String message : guestMessages) {
                redisTemplate.opsForList().rightPush(userKey, message);
            }
            redisTemplate.delete(guestKey); // 이전 완료 후 삭제
        }
    }
}
