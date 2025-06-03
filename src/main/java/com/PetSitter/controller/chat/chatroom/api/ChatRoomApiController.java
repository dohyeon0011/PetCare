package com.PetSitter.controller.chat.chatroom.api;

import com.PetSitter.dto.chat.chatroom.response.ChatRoomResponse;
import com.PetSitter.service.chat.chatroom.ChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "채팅방 전체 조회", description = "채팅방 전체 조회 API")
    @GetMapping("/members/{memberId}/chat/chat-rooms")
    public ResponseEntity<List<ChatRoomResponse.getChatRoomList>> findAllChatRooms(@PathVariable("memberId") @Parameter(required = true, description = "회원 고유 id") Long memberId) {
        log.info("ChatRoomApiController - findAllChatRooms() 호출 성공. memberId={}", memberId);
        List<ChatRoomResponse.getChatRoomList> chatRooms = chatRoomService.getAllChatRooms(memberId);
        log.info("find All: chatRooms = {}", chatRooms);

        return ResponseEntity.ok()
                .body(chatRooms);
    }

    @Operation(summary = "참여중인 특정 채팅방 조회", description = "참여중인 특정 채팅방 조회 API")
    @GetMapping("/members/{memberId}/chat/chat-rooms/{roomId}")
    public ResponseEntity<ChatRoomResponse.getChatRoomDetail> findChatRooms(@PathVariable("memberId") @Parameter(required = true, description = "회원 고유 id") Long memberId,
                                                                            @PathVariable("roomId") @Parameter(required = true, description = "채팅방 번호") String roomId) {
        log.info("ChatRoomApiController - findChatRooms() 호출 성공. memberId={}, roomId={}", memberId, roomId);
        ChatRoomResponse.getChatRoomDetail chatRoom = chatRoomService.getChatRoom(memberId, roomId);

        return ResponseEntity.ok()
                .body(chatRoom);
    }
}
