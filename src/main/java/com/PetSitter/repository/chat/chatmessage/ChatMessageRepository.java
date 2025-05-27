package com.PetSitter.repository.chat.chatmessage;

import com.PetSitter.domain.chat.ChatMessage;
import com.PetSitter.domain.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // 채팅방 id로 채팅 메시지 조회(채팅 메시지 전송 시간 기준 오름차순 조회)
    List<ChatMessage> findAllByChatRoomOrderBySentAtAsc(ChatRoom chatRoom);
}
