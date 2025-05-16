package com.PetSitter.service.chat.chatbot;

import com.PetSitter.dto.chat.chatbot.request.ChatMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatBotService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private final static Duration GUEST_CHAT_EXPIRATION = Duration.ofHours(12);
    private final static Duration USER_CHAT_EXPIRATION = Duration.ofHours(168); // 일주일

    private final static String CHAT_TYPE_CLIENT = "send";
    private final static String CHAT_TYPE_CHATBOT = "answer";

    // 챗봇 응답 키워드 맵
    private final Map<String, String> keywordResponses = Map.of(
            "예약", "예약은 로그인 후 이용 가능한 서비스로 아직 회원가입을 하시지 않으셨다면, 오른쪽 상단 '회원가입' 버튼을 눌러 진행해주세요. " +
                    "회원가입 이후 '마이페이지' 화면에서 키우는 반려견을 등록 후 '훈련사 프로필 > 자세히 보기 > 예약 버튼'을 통해 돌봄 예약을 진행하실 수 있습니다.!",
            "리뷰", "리뷰는 예약 완료 후 나의 예약 현황에서 돌봄 예약 클릭 후 작성 가능합니다.!",
            "회원가입", "저희 서비스는 기본적으로 '로그인' 후 이용 가능한 서비스로 회원가입은 오른쪽 상단 '회원가입' 버튼을 눌러 진행해주세요.!",
            "서비스", "저희는 반려견 돌봄 서비스를 제공합니다. 훈련사 프로필에서 고객들의 실제 리뷰를 보고 원하는 펫시터를 선택하여 예약해보세요.!",
            "내 정보", "'마이페이지' 화면에서 회원의 역할에 맞게 반려견을 등록하거나, '자격증'을 등록하고 '펫시터'인 경우 '돌봄 가능 날짜'를 직접 등록하여 돌봄 예약을 받을 수 있습니다.!"
    );

    // 게스트 채팅 기록 저장 및 봇 응답 처리
    public ChatMessage saveChatMessageForGuest(String uuid, String message) {
        // Redis 서버에 저장할 key 생성
        String key = "chat:guest:" + uuid;
        log.info("saveChatMessageForGuest() 호출: [key=" + key +", guest save message=" + message + "]");

        // 게스트가 전송한 채팅 직렬화 후 Redis 서버에 저장
        try {
            String userMessage = objectMapper.writeValueAsString(new ChatMessage(key, message, CHAT_TYPE_CLIENT));
            redisTemplate.opsForList().rightPush(key, userMessage);
        } catch (JsonProcessingException e) {
            log.error("saveChatMessageForGuest() - objectMapper 직렬화 중 에러 발생: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        // 챗봇이 응답한 메시지 직렬화 후 Redis 서버에 저장 -> 클라이언트 응답
        ChatMessage botResponse = new ChatMessage(key, getBotResponse(message), CHAT_TYPE_CHATBOT);
        log.info("saveChatMessageForGuest() 호출: [key=" + key +", bot save message=" + botResponse + "]");
        try {
            String botMessage = objectMapper.writeValueAsString(botResponse);
            redisTemplate.opsForList().rightPush(key, botMessage);
            redisTemplate.expire(key, GUEST_CHAT_EXPIRATION);
        } catch (JsonProcessingException e) {
            log.error("saveChatMessageForGuest() - botResponse 직렬화 중 에러 발생: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        return botResponse;
    }

    // 키워드 기반 봇 응답 로직
    private String getBotResponse(String message) {
        for (Map.Entry<String, String> entry : keywordResponses.entrySet()) {
            if (message.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return "죄송합니다. 이해하지 못했어요. '예약', '리뷰', '회원가입', '서비스', '내 정보' 등으로 질문해보세요.!";
    }

    // 로그인 유저 채팅 기록 저장 및 봇 응답 처리
    public ChatMessage saveChatMessageForUser(long memberId, String message) {
        // Redis 서버에 저장할 key 생성
        String key = "chat:user:" + memberId;
        log.info("saveChatMessageForUser() 호출:[key=" + key +", user save message=" + message + "]");

        // 유저가 전송한 채팅 직렬화 후 Redis 서버에 저장
        try {
            String userMessage = objectMapper.writeValueAsString(new ChatMessage(key, message, CHAT_TYPE_CLIENT));
            redisTemplate.opsForList().rightPush(key, userMessage);
        } catch (JsonProcessingException e) {
            log.error("saveChatMessageForUser() - objectMapper 직렬화 중 에러 발생: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        // 챗봇이 응답한 메시지 직렬화 후 Redis 서버에 저장 -> 클라이언트 응답
        ChatMessage botResponse = new ChatMessage(key, getBotResponse(message), CHAT_TYPE_CHATBOT);
        log.info("saveChatMessageForUser() 호출: [key=" + key +", bot save message=" + botResponse + "]");
        try {
            String botMessage = objectMapper.writeValueAsString(botResponse);  // 객체 -> JSON 문자열 직렬화
            redisTemplate.opsForList().rightPush(key, botMessage);
            redisTemplate.expire(key, USER_CHAT_EXPIRATION);
        } catch (JsonProcessingException e) {
            log.error("saveChatMessageForUser() - botResponse 직렬화 중 에러 발생: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        return botResponse;
    }

    // 게스트의 대화 기록 읽기
    public List<ChatMessage> getChatHistoryForGuest(String guestUUID) {
        // Redis 서버에 저장한 key 값
        String key = "chat:guest:" + guestUUID;
        log.info("getChatHistoryForGuest() 호출: [key=" + key + "]");

        // 게스트와 챗봇의 모든 대화 기록을 불러옴
        return Objects.requireNonNull(redisTemplate.opsForList().range(key, 0, -1), "역직렬화 실패. chat list is empty.")
                .stream()
                .filter(Objects::nonNull)
                .map(chatMessage -> {
                    try {
                        return objectMapper.readValue(chatMessage, ChatMessage.class);  // JSON 문자열 -> 객체 역직렬화
                    } catch (JsonProcessingException e) {
                        log.error("getChatHistoryForGuest() - objectMapper.readValue() 중 에러 발생: message={}, error={}", chatMessage, e.getMessage());
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    // 로그인 유저의 대화 기록 읽기
    public List<ChatMessage> getChatHistoryForUser(long memberId) {
        // Redis 서버에 저장한 key 값
        String key = "chat:user:" + memberId;
        log.info("getChatHistoryForUser() 호출: [key=" + key + "]");

        return Objects.requireNonNull(redisTemplate.opsForList().range(key, 0, -1), "역직렬화 실패. chat list is empty.") // 유저와 챗봇의 모든 대화 기록을 불러옴
                .stream()
                .filter(Objects::nonNull)
                .map(chatMessage -> {
                    try {
                        return objectMapper.readValue(chatMessage, ChatMessage.class);
                    } catch (JsonProcessingException e) {
                        log.error("getChatHistoryForUser() - objectMapper.readValue() 중 에러 발생: message={}, error={}", chatMessage, e.getMessage());
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    // 게스트 -> 로그인 유저 채팅 기록 마이그레이션
    public void migrateGuestChatToUser(String guestUUID, long memberId) {
        String guestKey = "chat:guest:" + guestUUID;
        String userKey = "chat:user:" + memberId;
        log.info("migrateGuestChatToUser 호출: [guestKey=" + guestKey +", userKey=" + userKey + "]");

        List<String> guestMessages = redisTemplate.opsForList().range(guestKey, 0, -1); // 게스트 키(chat:guest:{uuid}) 에 저장된 모든 메시지를 불러옴.
        if (guestMessages != null) {
            for (String message : guestMessages) {
                redisTemplate.opsForList().rightPush(userKey, message); // 사용자 메시지와 챗봇 응답 메시지 모두 마이그레이션
            }
            redisTemplate.delete(guestKey); // 이전 완료 후 삭제
        }
    }
}
