package com.PetSitter.repository.chat.chatroom;

import com.PetSitter.domain.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    // 발신자 id, 수신자 id로 채팅방 단건 조회
    Optional<ChatRoom> findBySenderIdAndReceiverId(Long senderId, Long receiverId);

    // 채팅방 id로 채팅방 단건 조회
    Optional<ChatRoom> findByRoomId(String roomId);

    // 발신자 id, 수신자 id로 채팅방 전체 조회
    List<ChatRoom> findAllBySenderIdAndReceiverId(Long senderId, Long receiverId);
}
