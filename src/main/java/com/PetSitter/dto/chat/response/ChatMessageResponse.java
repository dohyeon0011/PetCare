package com.PetSitter.dto.chat.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "채팅방 메시지 조회 Response DTO")
@NoArgsConstructor
@Getter
public class ChatMessageResponse {

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

    public ChatMessageResponse(Long roomId, Long senderId, Long receiverId, String message, LocalDateTime sentAt) {
        this.roomId = roomId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.sentAt = sentAt;
    }
}
