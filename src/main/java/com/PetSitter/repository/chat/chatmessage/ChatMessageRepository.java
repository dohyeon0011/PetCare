package com.PetSitter.repository.chat.chatmessage;

import com.PetSitter.domain.chat.ChatMessage;
import com.PetSitter.domain.chat.ChatRoom;
import com.PetSitter.dto.chat.response.UnreadMessageRes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // 채팅방 id로 채팅 메시지 조회(채팅 메시지 전송 시간 기준 오름차순 조회)
    List<ChatMessage> findAllByChatRoomOrderBySentAtAsc(ChatRoom chatRoom);

    // 사용자가 읽지 않은 메시지만 조회(읽지 않은 메시지 알림 탭)
    @Query("""
                select new com.PetSitter.dto.chat.response.UnreadMessageRes(
                    cm.chatRoom.id,
                    cm.chatRoom.roomId,
                    cm.sender.id,
                    cm.sender.name,
                    cm.message,
                    cm.sentAt
                )
                from ChatMessage cm
                where cm.receiver.id = :memberId and cm.isRead = false
                order by sentAt desc
            """)
    List<UnreadMessageRes> findUnreadMessagesByMemberId(@Param("memberId") Long memberId);
}
