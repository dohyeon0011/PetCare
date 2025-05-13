package com.PetSitter.dto.chat.chatbot.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Schema(description = "챗봇 이용 시 버튼 Req DTO")
public class ChatBotButtonRequest {
    @Schema(description = "회원 id")
    private Long memberId;     // 로그인 유저용

    @Schema(description = "guest UUID")
    private String guestUUID;    // 게스트용

    @Schema(description = "검색 키워드")
    private String keyword;      // 키워드 (공통)
}
