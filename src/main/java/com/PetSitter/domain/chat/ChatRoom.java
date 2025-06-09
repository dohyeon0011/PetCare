package com.PetSitter.domain.chat;

import com.PetSitter.domain.Member.Member;
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
@Table(name = "chat_room")
@Getter
@EntityListeners(AuditingEntityListener.class)
public class ChatRoom { // 채팅방 엔티티

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("대화방 번호")
    @Column(name = "room_id", unique = true, updatable = false)
    private String roomId;

    @Comment("발신자 회원")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member sender;

    @Comment("수신자 회원")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member receiver;

    @Comment("대화방 생성 시간")
    @Column(name = "created_at", updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    public ChatRoom(String roomId, Member sender, Member receiver) {
        this.roomId = roomId;
        addSender(sender);
        addReceiver(receiver);
    }

    @Comment("발신자 연관관계 설정 편의 메서드")
    private void addSender(Member sender) {
        this.sender = sender;
    }

    @Comment("수신자 연관관계 설정 편의 메서드")
    private void addReceiver(Member receiver) {
        this.receiver = receiver;
    }
}
