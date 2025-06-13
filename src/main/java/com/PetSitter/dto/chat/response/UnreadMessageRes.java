package com.PetSitter.dto.chat.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "사용자가 읽지 않은 메시지 조회 Res DTO(읽지 않은 메시지 알림 탭)")
@NoArgsConstructor
@Getter
public class UnreadMessageRes {

    @Schema(description = "채팅방 id")
    private Long roomId;

    @Schema(description = "채팅방 식별용 번호", pattern = "uuid")
    private String roomCode;

    @Schema(description = "발신자 id")
    private Long senderId;

    @Schema(description = "발신자 이름")
    private String senderName;

    @Schema(description = "발신 메시지")
    private String message;

    @Schema(description = "전송 시간")
    private LocalDateTime sentAt;

    public UnreadMessageRes(Long roomId, String roomCode, Long senderId, String senderName, String message, LocalDateTime sentAt) {
        this.roomId = roomId;
        this.roomCode = roomCode;
        this.senderId = senderId;
        this.senderName = senderName;
        this.message = message;
        this.sentAt = sentAt;
    }
}