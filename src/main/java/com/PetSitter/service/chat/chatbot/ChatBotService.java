package com.PetSitter.service.chat.chatbot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatBotService {

    private final RedisTemplate<String, String> redisTemplate;

    private final static Duration GUEST_CHAT_EXPIRATION = Duration.ofHours(12);
    private final static Duration USER_CHAT_EXPIRATION = Duration.ofHours(168); // 일주일

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
    public String saveChatMessageForGuest(String uuid, String message) {
        String key = "chat:guest:" + uuid;
        log.info("saveChatMessageForGuest() 호출: [key=" + key +", guest save message=" + message + "]");
        redisTemplate.opsForList().rightPush(key, "GUEST: " + message);

        String botResponse = getBotResponse(message);
        log.info("saveChatMessageForGuest() 호출: [key=" + key +", bot save message=" + botResponse + "]");
        redisTemplate.opsForList().rightPush(key, "BOT: " + botResponse);
        redisTemplate.expire(key, GUEST_CHAT_EXPIRATION);

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
    public String saveChatMessageForUser(long memberId, String message) {
        String key = "chat:user:" + memberId;
        log.info("saveChatMessageForUser() 호출:[key=" + key +", user save message=" + message + "]");
        redisTemplate.opsForList().rightPush(key, "USER: " + message);

        String botResponse = getBotResponse(message);
        log.info("saveChatMessageForUser() 호출: [key=" + key +", bot save message=" + botResponse + "]");
        redisTemplate.opsForList().rightPush(key, "BOT: " + botResponse);
        redisTemplate.expire(key, USER_CHAT_EXPIRATION);

        return botResponse;
    }

    // 게스트의 대화 기록 읽기
    public List<String> getChatHistoryForGuest(String guestUUID) {
        String key = "chat:guest:" + guestUUID;
        log.info("getChatHistoryForGuest() 호출: [key=" + key + "]");
        return redisTemplate.opsForList().range(key, 0, -1); // 게스트와 챗봇의 모든 대화 기록을 불러옴
    }

    // 로그인 유저의 대화 기록 읽기
    public List<String> getChatHistoryForUser(long memberId) {
        String key = "chat:user:" + memberId;
        log.info("getChatHistoryForUser() 호출: [key=" + key + "]");
        return redisTemplate.opsForList().range(key, 0, -1); // 유저와 챗봇의 모든 대화 기록을 불러옴
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
