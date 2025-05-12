package com.PetSitter.controller.chat.chatbot;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.MemberDetails;
import com.PetSitter.domain.Member.oauth.CustomOAuth2User;
import com.PetSitter.dto.chat.chatbot.request.ChatBotRequest;
import com.PetSitter.dto.chat.chatbot.request.GuestMigrationRequest;
import com.PetSitter.service.chat.redis.ChatBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatbot")
@Slf4j
public class ChatBotController {

    private final ChatBotService chatBotService;

    // 사용자 채팅 Output
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody ChatBotRequest request, BindingResult result, @AuthenticationPrincipal Object principal) {
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

        if (member != null) {
            chatBotService.saveChatMessageForUser(member.getId(), request.getMessage());
            log.info("[/api/chatbot/send: chatBotService.saveChatMessageForUser() 호출 성공]");
        } else {
            chatBotService.saveChatMessageForGuest(request.getGuestUUID(), request.getMessage());
            log.info("[/api/chatbot/send: chatBotService.saveChatMessageForGuest() 호출 성공]");
        }

        return ResponseEntity.ok()
                .build();
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
