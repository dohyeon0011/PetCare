package com.PetSitter.domain.chat;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.dto.chat.response.ChatMessageResponse;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "chat_message")
@Getter
@EntityListeners(AuditingEntityListener.class)
public class ChatMessage {  // 채팅방 메시지 엔티티

    @Id
    @GeneratedValue
    private Long id;

    @Comment("대화방(ChatRoom) 1 : N")
    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;

    @Comment("발신자 회원")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member sender;

    @Comment("수신자 회원")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member receiver;

    @Comment("전송 메시지")
    @Lob
    @Column(nullable = false)
    private String message;

    @Comment("채팅 전송 시간")
    @CreatedDate
    @Column(name = "sent_at", updatable = false)
    private LocalDateTime sentAt;

    @Builder
    public ChatMessage(ChatRoom chatRoom, Member sender, Member receiver, String message) {
        addChatRoom(chatRoom);
        addSender(sender);
        addReceiver(receiver);
        this.message = message;
    }

    @Comment("채팅방 연관관계 설정 편의 메서드")
    private void addChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    @Comment("발신자 연관관계 설정 편의 메서드")
    private void addSender(Member sender) {
        this.sender = sender;
    }

    @Comment("수신자 연관관계 설정 편의 메서드")
    private void addReceiver(Member receiver) {
        this.receiver = receiver;
    }

    @Comment("ChatMessage Entity -> Response DTO Method")
    public ChatMessageResponse.messageDto toChatMessageResponse() {
        return new ChatMessageResponse.messageDto(this.id, this.chatRoom.getId(), this.sender.getId(), this.receiver.getId(), this.message, this.getSentAt());
    }
}
