package com.PetSitter.dto.chat.chatroom.response;

import com.PetSitter.domain.chat.ChatMessage;
import com.PetSitter.dto.chat.response.ChatMessageResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "채팅방 조회 Response DTO")
public class ChatRoomResponse {

    @Schema(description = "채팅방 리스트 조회 Response DTO")
    @NoArgsConstructor
    @Getter
    public static class getChatRoomList {
        @Schema(description = "채팅방 고유 id")
        private Long id;

        @Schema(description = "채팅방 번호")
        private String roomId;

        @Schema(description = "수신자 이름")
        private String receiverName;

        @Schema(description = "최근 송/수신 메시지")
        private String latestMessage;

        @Schema(description = "최근 송/수신 시간")
        private LocalDateTime latestAt;

        public getChatRoomList(Long id, String roomId, String receiverName, String latestMessage, LocalDateTime latestAt) {
            this.id = id;
            this.roomId = roomId;
            this.receiverName = receiverName;
            this.latestMessage = latestMessage;
            this.latestAt = latestAt;
        }
    }

    @Schema(description = "참여중인 특정 채팅방 조회 Response DTO")
    @NoArgsConstructor
    @Getter
    public static class getChatRoomDetail {
        @Schema(description = "채팅방 고유 id")
        private Long id;

        @Schema(description = "채팅방 번호")
        private String roomId;

        @Schema(description = "발신자 id")
        private Long senderId;

        @Schema(description = "수신자 id")
        private Long receiverId;

        @Schema(description = "채팅 메시지 내역")
        List<ChatMessageResponse.chatMessageDto> chatMessages = new ArrayList<>();

        public getChatRoomDetail(Long id, String roomId, Long senderId, Long receiverId, List<ChatMessage> chatMessages) {
            this.id = id;
            this.roomId = roomId;
            this.senderId = senderId;
            this.receiverId = receiverId;
            this.chatMessages = chatMessages.stream()
                    .map(ChatMessageResponse.chatMessageDto::new)
                    .toList();
        }
    }
}
