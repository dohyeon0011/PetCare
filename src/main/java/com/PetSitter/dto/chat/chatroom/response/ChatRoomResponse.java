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

        @Schema(description = "수신자 id")
        private Long receiverId;

        @Schema(description = "수신자 이름")
        private String receiverName;

        @Schema(description = "최근 송/수신 메시지")
        private String latestMessage;

        @Schema(description = "최근 송/수신 시간")
        private LocalDateTime latestAt;

        @Schema(description = "안 읽은 메시지 개수")
        private Long unreadMessageCount;

        public getChatRoomList(Long id, String roomId, Long receiverId, String receiverName, String latestMessage, LocalDateTime latestAt, Long unreadMessageCount) {
            this.id = id;
            this.roomId = roomId;
            this.receiverId = receiverId;
            this.receiverName = receiverName;
            this.latestMessage = latestMessage;
            this.latestAt = latestAt;
            this.unreadMessageCount = unreadMessageCount;
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

        @Schema(description = "수신자 이름")
        private String receiverName;

        @Schema(description = "채팅 메시지 내역")
        List<ChatMessageResponse.chatMessageDto> chatMessages = new ArrayList<>();

        public getChatRoomDetail(Long id, String roomId, Long senderId, Long receiverId, String receiverName, List<ChatMessage> chatMessages) {
            this.id = id;
            this.roomId = roomId;
            this.senderId = senderId;
            this.receiverId = receiverId;
            this.receiverName = receiverName;
            this.chatMessages = chatMessages.stream()
                    .map(ChatMessageResponse.chatMessageDto::new)
                    .toList();
        }
    }

    @Schema(description = "특정 돌봄사와의 진행중인 채팅방 존재 여부 확인 Response DTO")
    @NoArgsConstructor
    @Getter
    public static class getExistsChatRoomDetail {
        @Schema(description = "채팅방 번호")
        private String roomId;

        @Schema(description = "수신자 id")
        private Long receiverId;

        @Schema(description = "수신자 이름")
        private String receiverName;

        public getExistsChatRoomDetail(String roomId, Long receiverId, String receiverName) {
            this.roomId = roomId;
            this.receiverId = receiverId;
            this.receiverName = receiverName;
        }
    }
}
