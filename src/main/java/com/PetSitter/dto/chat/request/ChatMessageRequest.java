package com.PetSitter.dto.chat.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Schema(description = "고객 - 돌봄사 간 채팅방 채팅 메시지 Req DTO")
public class ChatMessageRequest {

    @Schema(description = "발신자 id")
    @NotNull(message = "발신자 id 값은 필수입니다.")
    private Long senderId;

    @Schema(description = "수신자 id")
    @NotNull(message = "수신자 id 값은 필수입니다.")
    private Long receiverId;

    @Schema(description = "전송할 채팅 메시지")
    @Lob
    @NotBlank(message = "전송할 채팅 메시지 필드 값은 필수입니다.")
    private String message;
}
