package com.PetSitter.dto.chat.response;

import com.PetSitter.domain.chat.ChatMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
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

        @Schema(description = "채팅방 식별용 번호", pattern = "uuid")
        private String roomCode;

        @Schema(description = "발신자 id")
        private Long senderId;

        @Schema(description = "발신자 이름(채팅 알림 발신자 이름 표시용)")
        private String senderName;

        @Schema(description = "수신자 id")
        private Long receiverId;

        @Schema(description = "전송된 채팅방 메시지")
        private String message;

        @Schema(description = "전송 시간")
        private LocalDateTime sentAt;

        @Schema(description = "대화방 최초 생성 or 기존 채팅방 여부")
        @JsonProperty("isNew")
        private boolean isNew;

        @Schema(description = "읽음 여부")
        @JsonProperty("isRead")
        private boolean isRead;

        public messageDto(Long id, Long roomId, String roomCode, Long senderId, String senderName, Long receiverId, String message, LocalDateTime sentAt, boolean isNew, boolean isRead) {
            this.id = id;
            this.roomId = roomId;
            this.roomCode = roomCode;
            this.senderId = senderId;
            this.senderName = senderName;
            this.receiverId = receiverId;
            this.message = message;
            this.sentAt = sentAt;
            this.isNew = isNew;
            this.isRead = isRead;
        }

        @Override
        public String toString() {
            return "messageDto{" +
                    "id=" + id +
                    ", roomId=" + roomId +
                    ", roomCode='" + roomCode + '\'' +
                    ", senderId=" + senderId +
                    ", senderName='" + senderName + '\'' +
                    ", receiverId=" + receiverId +
                    ", message='" + message + '\'' +
                    ", sentAt=" + sentAt +
                    ", isNew=" + isNew +
                    ", isRead=" + isRead +
                    '}';
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

        @Schema(description = "읽음 여부")
        @JsonProperty("is_read")
        private boolean isRead;

        public chatMessageDto(ChatMessage chatMessage) {
            this.id = chatMessage.getId();
            this.senderId = chatMessage.getSender().getId();
            this.senderName = chatMessage.getSender().getName();
            this.receiverId = chatMessage.getReceiver().getId();
            this.receiverName = chatMessage.getReceiver().getName();
            this.message = chatMessage.getMessage();
            this.sentAt = chatMessage.getSentAt();
            this.isRead = chatMessage.isRead();
        }
    }
}
