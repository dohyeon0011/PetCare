package com.PetSitter.dto.chat.chatroom.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "채팅방 조회 DTO Response")
public class ChatRoomResponse {

    @Schema(description = "채팅방 리스트 조회 DTO Response")
    @NoArgsConstructor
    @Getter
    public static class getChatRoomList {
        @Schema(description = "채팅방 고유 id")
        private Long id;

        @Schema(description = "채팅방 번호")
        private String roomId;

        @Schema(description = "수신자 이름")
        private String receiverName;

        @Schema(description = "최근 송/수신 메시지")
        private String latestMessage;

        @Schema(description = "최근 송/수신 시간")
        private LocalDateTime latestAt;

        public getChatRoomList(Long id, String roomId, String receiverName, String latestMessage, LocalDateTime latestAt) {
            this.id = id;
            this.roomId = roomId;
            this.receiverName = receiverName;
            this.latestMessage = latestMessage;
            this.latestAt = latestAt;
        }
    }
}
