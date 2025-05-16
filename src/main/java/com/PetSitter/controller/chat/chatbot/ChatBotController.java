package com.PetSitter.controller.chat.chatbot;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.MemberDetails;
import com.PetSitter.domain.Member.oauth.CustomOAuth2User;
import com.PetSitter.dto.chat.chatbot.request.ChatBotButtonRequest;
import com.PetSitter.dto.chat.chatbot.request.ChatBotRequest;
import com.PetSitter.dto.chat.chatbot.request.ChatMessage;
import com.PetSitter.dto.chat.chatbot.request.GuestMigrationRequest;
import com.PetSitter.service.chat.chatbot.ChatBotService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets-care/chatbot")
@Slf4j
public class ChatBotController {

    private final ChatBotService chatBotService;

    // 버튼 클릭 응답
    @PostMapping("/button")
    public ResponseEntity<ChatMessage> buttonClick(@RequestBody ChatBotButtonRequest request) throws JsonProcessingException {
        ChatMessage botResponse = null;
        if (request.getMemberId() != null) {
            botResponse = chatBotService.saveChatMessageForUser(request.getMemberId(), request.getKeyword());
        } else if (request.getGuestUUID() != null) {
            botResponse = chatBotService.saveChatMessageForGuest(request.getGuestUUID(), request.getKeyword());
        }

        return ResponseEntity.ok()
                .body(botResponse);
    }

    // 버튼 목록 제공
    // 로그인 여부와 관계없이 버튼 목록을 반환하는 API
    @GetMapping("/buttons")
    public ResponseEntity<?> getButtons() {
        // 비로그인 상태일 경우와 로그인 상태일 경우 버튼 목록을 다르게 처리
        List<Map<String, String>> buttons = List.of(
                    Map.of("label", "예약 방법", "keyword", "예약"),
                    Map.of("label", "리뷰 작성", "keyword", "리뷰"),
                    Map.of("label", "회원가입", "keyword", "회원가입"),
                    Map.of("label", "서비스 소개", "keyword", "서비스"),
                    Map.of("label", "내 정보", "keyword", "내 정보")
        );
        return ResponseEntity.ok(buttons);
    }

    // 사용자 채팅 Output
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody ChatBotRequest request, BindingResult result, @AuthenticationPrincipal Object principal) throws JsonProcessingException {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();

            result.getFieldErrors().forEach(error -> {
                errors.put(error.getField(), error.getDefaultMessage());
            });

            return ResponseEntity.badRequest()
                    .body(errors);
        }

        Member member = null;
        if (principal instanceof MemberDetails) {
            member = ((MemberDetails) principal).getMember();
        } else if (principal instanceof CustomOAuth2User) {
            member = ((CustomOAuth2User) principal).getMember();
        }

        ChatMessage botResponse;
        if (member != null) {
            botResponse = chatBotService.saveChatMessageForUser(member.getId(), request.getMessage());
            log.info("[/api/chatbot/send: chatBotService.saveChatMessageForUser() 호출 성공]");
        } else {
            botResponse = chatBotService.saveChatMessageForGuest(request.getGuestUUID(), request.getMessage());
            log.info("[/api/chatbot/send: chatBotService.saveChatMessageForGuest() 호출 성공]");
        }

        return ResponseEntity.ok()
                .body(botResponse);
    }

    // 로그인된 유저 또는 게스트의 챗봇 대화 기록을 불러오기
    @GetMapping("/history")
    public ResponseEntity<List<ChatMessage>> getChatHistory(@AuthenticationPrincipal Object principal, @RequestParam(required = false) String guestUUID) {
        Member member = null;
        if (principal instanceof MemberDetails) {
            member = ((MemberDetails) principal).getMember();
        } else if (principal instanceof CustomOAuth2User) {
            member = ((CustomOAuth2User) principal).getMember();
        }

        List<ChatMessage> chatHistory;
        if (member != null) {
            chatHistory = chatBotService.getChatHistoryForUser(member.getId()); // 유저 대화 기록
        } else {
            if (guestUUID == null) {
                return ResponseEntity.badRequest()
                        .build();
            }
            chatHistory = chatBotService.getChatHistoryForGuest(guestUUID); // 게스트 대화 기록 불러오기
        }

        return ResponseEntity.ok()
                .body(chatHistory);
    }

    // 비회원 -> 로그인 채팅 기록 마이그레이션
    @PostMapping("/migrate")
    public ResponseEntity<?> migrateChat(@RequestBody GuestMigrationRequest request, BindingResult result, @AuthenticationPrincipal Object principal) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();

            result.getFieldErrors().forEach(error -> {
                errors.put(error.getField(), error.getDefaultMessage());
            });

            return ResponseEntity.badRequest()
                    .body(errors);
        }

        Member member = null;
        if (principal instanceof MemberDetails) {
            member = ((MemberDetails) principal).getMember();
        } else if (principal instanceof CustomOAuth2User) {
            member = ((CustomOAuth2User) principal).getMember();
        }
        chatBotService.migrateGuestChatToUser(request.getGuestUUID(), member.getId());
        log.info("[/api/chatbot/migrate: chatBotService.migrateGuestChatToUser() 호출 성공]");

        return ResponseEntity.ok()
                .build();
    }
}
