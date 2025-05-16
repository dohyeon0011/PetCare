package com.PetSitter.dto.chat.chatbot.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor
@Getter
@Schema(description = "채팅 메시지 Response DTO")
public class ChatMessage {
    @Schema(description = "전송자", pattern = "guestUUid, user id")
    private String sender;  // guestUUID, user id

    @Schema(description = "전송 메시지")
    private String message;

    @Schema(description = "전송 시간")
    private String timestamp;   // 전송 시간

    @Schema(description = "전송 유형", pattern = "send(사용자 전송), answer(챗봇 응답)")
    private String type;    // send, answer

    public ChatMessage(String sender, String message, String type) {
        this.sender = sender;
        this.message = message;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.type = type;
    }
}
