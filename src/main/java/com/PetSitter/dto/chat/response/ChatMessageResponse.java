package com.PetSitter.dto.chat.response;

import com.PetSitter.domain.chat.ChatMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "채팅방 메시지 조회 Response DTO")
public class ChatMessageResponse {

    @Schema(description = "전송한 메시지 DTO")
    @NoArgsConstructor
    @Getter
    public static class messageDto {
        @Schema(description = "채팅 메시지 id")
        private Long id;

        @Schema(description = "채팅방 id")
        private Long roomId;

        @Schema(description = "발신자 id")
        private Long senderId;

        @Schema(description = "수신자 id")
        private Long receiverId;

        @Schema(description = "전송된 채팅방 메시지")
        private String message;

        @Schema(description = "전송 시간")
        private LocalDateTime sentAt;

        public messageDto(Long id, Long roomId, Long senderId, Long receiverId, String message, LocalDateTime sentAt) {
            this.id = id;
            this.roomId = roomId;
            this.senderId = senderId;
            this.receiverId = receiverId;
            this.message = message;
            this.sentAt = sentAt;
        }
    }

    @Schema(description = "특정 채팅방 메시지 조회 DTO")
    @NoArgsConstructor
    @Getter
    public static class chatMessageDto {
        @Schema(description = "채팅 메시지 id")
        private Long id;

        @Schema(description = "발신자 id")
        private Long senderId;

        @Schema(description = "발신자 이름")
        private String senderName;

        @Schema(description = "수신자 id")
        private Long receiverId;

        @Schema(description = "수신자 이름")
        private String receiverName;

        @Schema(description = "전송된 채팅방 메시지")
        private String message;

        @Schema(description = "전송 시간")
        private LocalDateTime sentAt;

        public chatMessageDto(ChatMessage chatMessage) {
            this.id = chatMessage.getId();
            this.senderId = chatMessage.getSender().getId();
            this.senderName = chatMessage.getSender().getName();
            this.receiverId = chatMessage.getReceiver().getId();
            this.receiverName = chatMessage.getReceiver().getName();
            this.message = chatMessage.getMessage();
            this.sentAt = chatMessage.getSentAt();
        }
    }
}
