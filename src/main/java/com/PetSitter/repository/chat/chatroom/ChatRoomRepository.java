package com.PetSitter.repository.chat.chatroom;

import com.PetSitter.domain.chat.ChatRoom;
import com.PetSitter.dto.chat.chatroom.response.ChatRoomResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // 회원의 진행중인 채팅방 전체 조회
    @Query(value = """
            select
                cr.id as id,
                cr.room_id as roomId,
                receiver.name as receiverName,
                cm.message as latestMessage,
                cm.sent_at as latestAt
            from chat_room cr
            join members sender on cr.sender_member_id = sender_member_id
            join members receiver on cr.receiver_member_id = receiver_member_id
            join (
                select *
                    from (
                        select *,
                           ROW_NUMBER() OVER (PARTITION BY chat_room_id order by sent_at DESC) as rn
                        from chat_message
                ) ranked
                where rn = 1
            ) cm on cm.chat_room_id = cr.id
            where cr.sender_member_id = :memberId
            order by cm.sent_at desc
            """,
            nativeQuery = true
    )
    List<ChatRoomResponse.getChatRoomList> findAllByMemberId(@Param("memberId") Long memberId);
}
