package com.PetSitter.dto.chat.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "채팅 메시지 읽음 여부 필드 수정 후 Res DTO")
@NoArgsConstructor
@Getter
public class ReadStatusUpdateMessageRes {

    @Schema(description = "채팅 메시지 id")
    private Long id;

    @Schema(description = "읽음 여부", pattern = "true, false")
    @JsonProperty("isRead")
    private boolean isRead;

    public ReadStatusUpdateMessageRes(Long id, boolean isRead) {
        this.id = id;
        this.isRead = isRead;
    }
}
