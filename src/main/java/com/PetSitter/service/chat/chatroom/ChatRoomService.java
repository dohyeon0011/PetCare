package com.PetSitter.service.chat.chatroom;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.chat.ChatRoom;
import com.PetSitter.repository.Member.MemberRepository;
import com.PetSitter.repository.chat.chatroom.ChatRoomRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    /**
     * 채팅방 생성 Or 불러오기
     */
    public ChatRoom createOrGetChatRoom(Long senderId, Long receiverId) {
        ChatRoom chatRoom = chatRoomRepository.findBySenderIdAndReceiverId(senderId, receiverId)
                .orElseGet(() -> {  // 발신자 - 수신자가 최초 대화할 경우, 새로운 채팅방 엔티티 생성
                    Member sender = memberRepository.findById(senderId)
                            .orElseThrow(() -> new EntityNotFoundException("발신자 엔티티 조회 오류: [senderId=" + senderId + "]"));
                    Member receiver = memberRepository.findById(receiverId)
                            .orElseThrow(() -> new EntityNotFoundException("수신자 엔티티 조회 오류: [receiverId=" + receiverId + "]"));

                    ChatRoom newChatRoom = ChatRoom.builder()
                            .roomId(UUID.randomUUID().toString())
                            .sender(sender)
                            .receiver(receiver)
                            .build();

                    log.info("ChatRoomService: createOrGetChatRoom() - ChatRoom Entity Create Success. id={}", newChatRoom.getId());
                    return chatRoomRepository.save(newChatRoom);
                });

        log.info("ChatRoomService: createOrGetChatRoom() - ChatRoom Entity Loading Success. id={}", chatRoom.getRoomId());
        return chatRoom;
    }
}
