package com.PetSitter.dto.chat.chatbot.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Schema(description = "챗봇 채팅 Req DTO")
public class ChatBotRequest {
    @Schema(description = "게스트인 경우 식별 UUID")
    private String guestUUID;

    @Schema(description = "전송할 메시지")
    private String message;
}
