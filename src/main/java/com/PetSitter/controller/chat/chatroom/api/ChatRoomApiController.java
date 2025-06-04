package com.PetSitter.controller.chat.chatroom.api;

import com.PetSitter.dto.chat.chatroom.response.ChatRoomResponse;
import com.PetSitter.service.chat.chatroom.ChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    @GetMapping("/members/{memberId}/chat/chat-rooms/{roomId}/messages")
    public ResponseEntity<ChatRoomResponse.getChatRoomDetail> findChatRooms(@PathVariable("memberId") @Parameter(required = true, description = "회원 고유 id") Long memberId,
                                                                            @PathVariable("roomId") @Parameter(required = true, description = "채팅방 번호") String roomId) {
        log.info("ChatRoomApiController - findChatRooms() 호출 성공. memberId={}, roomId={}", memberId, roomId);
        ChatRoomResponse.getChatRoomDetail chatRoom = chatRoomService.getChatRoom(memberId, roomId);

        return ResponseEntity.ok()
                .body(chatRoom);
    }

    @Operation(summary = "특정 돌봄사와의 진행중인 채팅방 존재 여부 확인", description = "특정 돌봄사와의 진행중인 채팅방 존재 여부 확인 API")
    @GetMapping("/members/{memberId}/chat/chat-rooms-exists")
    public ResponseEntity<Optional<ChatRoomResponse.getExistsChatRoomDetail>> existsChatRooms(@PathVariable("memberId") @Parameter(required = true, description = "회원 고유 id") Long senderId,
                                             @RequestParam("sitterId") @Parameter(description = "수신자(돌봄사) 고유 id") Long receiverId) {
        log.info("ChatRoomApiController - existsChatRooms() 호출 성공. senderId={}, receiverId={}", senderId, receiverId);
        Optional<ChatRoomResponse.getExistsChatRoomDetail> chatRoom = chatRoomService.existsChatRooms(senderId, receiverId);

        chatRoom.ifPresentOrElse(
                cr -> log.info("Existing ChatRoom found. roomId={}, receiverId={}", cr.getRoomId(), cr.getReceiverId()),
                () -> log.info("None Existing ChatRoom found for senderId={}, receiverId={}", senderId, receiverId)
        );

        return ResponseEntity.ok()
                .body(chatRoom);
    }
}
