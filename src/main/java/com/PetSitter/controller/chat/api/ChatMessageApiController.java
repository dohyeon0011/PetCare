package com.PetSitter.controller.chat.api;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.MemberDetails;
import com.PetSitter.domain.Member.oauth.CustomOAuth2User;
import com.PetSitter.domain.chat.ChatMessage;
import com.PetSitter.dto.chat.request.ChatMessageRequest;
import com.PetSitter.service.chat.chatmessage.ChatMessageService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
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
@RequestMapping("/api/pets-care/chat")
@Slf4j
public class ChatMessageApiController {

    private final ChatMessageService chatMessageService;

    @Operation(summary = "채팅방 채팅 메시지 전송", description = "채팅방 채팅 메시지 전송 API")
    @MessageMapping("/message")
    public ResponseEntity<?> saveChatMessage(@RequestBody @Valid ChatMessageRequest chatMessageRequest, BindingResult result, @AuthenticationPrincipal Object principal) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();

            result.getFieldErrors().forEach(error -> {
                errors.put(error.getField(), error.getDefaultMessage());
            });
            log.error("ChatMessageApiController - saveChatMessage() 에러 발생, errors={}", errors);

            return ResponseEntity.badRequest()
                    .body(errors);
        }

        Member member = null;
        if (principal instanceof MemberDetails) {
            member = ((MemberDetails) principal).getMember();
        } else if (principal instanceof CustomOAuth2User) {
            member = ((CustomOAuth2User) principal).getMember();
        }

        ChatMessage chatMessage = null;
//        if (member != null) {
            chatMessage = chatMessageService.saveMessage(chatMessageRequest.getRoomId(), chatMessageRequest.getSenderId(),
                    chatMessageRequest.getReceiverId(), chatMessageRequest.getMessage());
//        }

        return ResponseEntity.ok()
                .body(chatMessage);
    }
}
