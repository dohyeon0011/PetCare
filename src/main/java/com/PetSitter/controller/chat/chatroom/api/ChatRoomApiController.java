package com.PetSitter.controller.chat.chatroom.api;

import com.PetSitter.dto.chat.chatroom.response.ChatRoomResponse;
import com.PetSitter.service.chat.chatroom.ChatRoomService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets-care")
@Slf4j
public class ChatRoomApiController {

    private final ChatRoomService chatRoomService;

    @GetMapping("/members/{memberId}/chat/chat-rooms")
    public ResponseEntity<List<ChatRoomResponse.getChatRoomList>> findAllChatRooms(@PathVariable("memberId") @Parameter(required = true, description = "회원 고유 id") Long memberId) {
        log.info("ChatRoomApiController - findAllChatRooms() 호출 성공. memberId={}", memberId);
        List<ChatRoomResponse.getChatRoomList> chatRooms = chatRoomService.getAllChatRooms(memberId);
        log.info("find All: chatRooms = {}", chatRooms);

        return ResponseEntity.ok()
                .body(chatRooms);
    }
}
