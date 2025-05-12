package com.PetSitter.dto.chat.chatbot.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Schema(description = "비회원 챗봇 채팅 기록 마이그레이션 Req DTO")
public class GuestMigrationRequest {
    @Schema(description = "게스트 식별 UUID")
    @NotBlank(message = "게스트 식별 UUID 값은 필수입니다.")
    private String guestUUID;
}
