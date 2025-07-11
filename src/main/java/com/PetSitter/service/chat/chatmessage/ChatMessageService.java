package com.PetSitter.service.chat.chatmessage;

import com.PetSitter.config.annotation.ReadOnlyTransactional;
import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.chat.ChatMessage;
import com.PetSitter.domain.chat.ChatRoom;
import com.PetSitter.dto.chat.response.ChatMessageResponse;
import com.PetSitter.dto.chat.response.ReadStatusUpdateMessageRes;
import com.PetSitter.dto.chat.response.UnreadMessageRes;
import com.PetSitter.repository.Member.MemberRepository;
import com.PetSitter.repository.chat.chatmessage.ChatMessageRepository;
import com.PetSitter.repository.chat.chatroom.ChatRoomRepository;
import com.PetSitter.service.chat.chatroom.ChatRoomService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomService chatRoomService;
    private final MemberRepository memberRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private static final String destination = "/queue/chat/messages";

    /**
     * 채팅 메시지 생성
     */
    @Transactional
    public ChatMessageResponse.messageDto saveMessage(String roomId, Long senderId, Long receiverId, String message) {
        // 채팅방 조회
        ChatRoom chatRoom;
        boolean isNew = false;
        if (roomId.equals("new")) { // 새로운 대화를 시작할 경우
            log.info("ChatMessageService - saveMessage(): 새 채팅방 생성 후 메시지 저장: senderId={}, receiverId={}", senderId, receiverId);
            chatRoom = chatRoomService.createOrGetChatRoom(senderId, receiverId);
            isNew = true;
        } else {    // 기존 채팅방에서 메시지를 전송할 경우
            log.info("ChatMessageService - saveMessage(): 기존 채팅방 메시지 저장: roomId={}", roomId);
            chatRoom = chatRoomRepository.findByRoomId(roomId)
                    .orElseThrow(() -> new EntityNotFoundException("채팅방 엔티티 조회 오류. [ChatRoom id=" + roomId + "]"));
        }

        // 발신자, 수신자 조회
        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("발신자 엔티티 조회 오류: [senderId=" + senderId + "]"));
        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new EntityNotFoundException("수신자 엔티티 조회 오류: [receiverId=" + receiverId + "]"));

        // 채팅 메시지 엔티티 생성
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .receiver(receiver)
                .message(message)
                .build();
        log.info("ChatMessageService - saveMessage(): ChatMessage Entity Create Success.");
        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);
        ChatMessageResponse.messageDto chatMessageResponse = savedMessage.toChatMessageResponse(isNew);

        // 수신자(해당 경로 구독자)에게 메시지 전송
        messagingTemplate.convertAndSendToUser(receiver.getLoginId(), destination, chatMessageResponse);

        return chatMessageResponse;
    }

    /**
     * 채팅 메시지 읽음 처리
     */
    @Transactional
    public void messageAsRead(Long messageId) {
        log.info("ChatMessageService - messageAsRead(): 메시지 읽음 처리. id={}", messageId);
        ChatMessage findChatMessage = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("ChatMessageService - messageAsRead(): ChatMessage Not found. [id=" + messageId + "]"));
        findChatMessage.changeRead();

        messagingTemplate.convertAndSendToUser(findChatMessage.getSender().getLoginId(),
                "/queue/chat/messages/read-updates",
                new ReadStatusUpdateMessageRes(findChatMessage.getId(), findChatMessage.isRead()));
    }

    /**
     * 사용자가 읽지 않은 메시지만 조회(읽지 않은 메시지 알림 탭)
     * @ReadOnlyTransactional: 커스텀 읽기 전용 어노테이션
     */
    @ReadOnlyTransactional
    public List<UnreadMessageRes> findUnreadMessages(Long memberId) {
        log.info("ChatMessageService - findUnreadMessages(): 읽지 않은 메시지 조회. memberId={}", memberId);
        return chatMessageRepository.findUnreadMessagesByMemberId(memberId);
    }
}
