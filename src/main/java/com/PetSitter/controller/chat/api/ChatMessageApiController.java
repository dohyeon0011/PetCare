package com.PetSitter.controller.chat.api;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.MemberDetails;
import com.PetSitter.domain.Member.oauth.CustomOAuth2User;
import com.PetSitter.dto.chat.request.ChatMessageRequest;
import com.PetSitter.dto.chat.response.ChatMessageResponse;
import com.PetSitter.service.chat.chatmessage.ChatMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets-care/chat")
@Slf4j
public class ChatMessageApiController {

    private final ChatMessageService chatMessageService;

    @Operation(summary = "채팅방 채팅 메시지 전송", description = "채팅방 채팅 메시지 전송 API")
    @MessageMapping("/chat-room/{roomId}/message")    // prefix, endpoint 설정을 포함한 입력한 url로 발행된 메세지를 뿌림.(Websocket으로 STOMP 통신)
//    @SendToUser("/queue/chat-message")   // 해당 메서드의 return 값을 url을 구독하는 클라이언트에게 메세지 발행.(1:1 관계, 1:N일 경우 @SendTo, 경로 파라미터를 허용 X(이유: WebSocket 메시지를 사용자에게 보낼 때는 해당 유저가 고정된 경로(/user/queue/xxx)를 구독하고 있어야 하기 때문. PathVariable이 있으면 경로가 유동적이라 클라이언트가 정해진 경로를 구독할 수 없음.))
                                         // /user/A/queue/chat-message 이런 식으로 Spring 내부에서 로그인 사용자 ID로 자동 분리해서 처리함.(자기 자신한테 보내고 받기. 상대에게 보내려면 convertAndSendToUser()로 명시적으로 보내야 함.)
    @PreAuthorize("isAuthenticated()")  //  WebSocket 세션 인증 정보(accessor.setUser(), 해당 메서드나 클래스가 호출되기 전에 인증 정보를 먼저 파악.)
    public ResponseEntity<?> saveChatMessage(@DestinationVariable("roomId") @Parameter(required = true, description = "채팅창 고유 번호") String roomId, // 웹소켓 메세징의 경우 @PathVariable이 아닌 @DestinationVariable(구독 및 발행 url의 path parameter)을 사용해야 함.
                                             @Payload @Valid ChatMessageRequest chatMessageRequest, BindingResult result,   // 웹소켓 메시지는 @Payload로 ChatMessageRequest 객체에 매핑
                                             Principal principal) {
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
        } else if (principal == null) {
            log.warn("WebSocket principal 인증 정보 없음. principal: {}", principal);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("인증되지 않은 사용자입니다.");
        }

        ChatMessageResponse chatMessage = null;
        if (member != null) {
             chatMessage = chatMessageService.saveMessage(roomId, chatMessageRequest.getSenderId(),
                    chatMessageRequest.getReceiverId(), chatMessageRequest.getMessage());
        }
        log.info("Create: ChatMessage = {}", chatMessage);

        return ResponseEntity.ok()
                .body(chatMessage);
    }
}
