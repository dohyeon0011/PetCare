package com.PetSitter.service.chat.chatmessage;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.Role;
import com.PetSitter.domain.chat.ChatMessage;
import com.PetSitter.domain.chat.ChatRoom;
import com.PetSitter.repository.Member.MemberRepository;
import com.PetSitter.repository.chat.chatmessage.ChatMessageRepository;
import com.PetSitter.repository.chat.chatroom.ChatRoomRepository;
import com.PetSitter.service.chat.chatroom.ChatRoomService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomService chatRoomService;
    private final MemberRepository memberRepository;

    /**
     * 채팅 메시지 생성
     */
    public ChatMessage saveMessage(String roomId, Long senderId, Long receiverId, String message) {
        // 1. 채팅방 조회
        ChatRoom chatRoom = null;
        if (roomId != null && !roomId.isBlank()) {   // 기존 채팅방에서 메시지를 전송할 경우
            log.info("ChatMessageService - saveMessage(): 기존 채팅방 메시지 저장: roomId={}", roomId);
            chatRoom = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new EntityNotFoundException("채팅방 엔티티 조회 오류. [ChatRoom id=" + roomId + "]"));
        } else {    // 새로운 대화를 시작할 경우
            log.info("ChatMessageService - saveMessage(): 새 채팅방 생성 후 메시지 저장: senderId={}, receiverId={}", senderId, receiverId);
            chatRoom = chatRoomService.createOrGetChatRoom(senderId, receiverId);
        }

        // 2. 발신자, 수신자 조회
        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("발신자 엔티티 조회 오류: [senderId=" + senderId + "]"));
        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new EntityNotFoundException("수신자 엔티티 조회 오류: [receiverId=" + receiverId + "]"));

        // 3. 채팅 메시지 엔티티 생성
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .receiver(receiver)
                .message(message)
                .build();
        log.info("ChatMessageService: saveMessage() - ChatMessage Entity Create Success. id={}", chatMessage.getId());

        return chatMessageRepository.save(chatMessage);
    }

}
