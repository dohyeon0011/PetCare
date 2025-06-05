package com.PetSitter.service.chat.chatroom;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.chat.ChatMessage;
import com.PetSitter.domain.chat.ChatRoom;
import com.PetSitter.dto.chat.chatroom.response.ChatRoomResponse;
import com.PetSitter.repository.Member.MemberRepository;
import com.PetSitter.repository.chat.chatmessage.ChatMessageRepository;
import com.PetSitter.repository.chat.chatroom.ChatRoomRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Comment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Comment("채팅방 생성 Or 불러오기")
    public ChatRoom createOrGetChatRoom(Long senderId, Long receiverId) {
        log.info("ChatRoomService - createOrGetChatRoom() 호출 성공.");
        ChatRoom chatRoom = chatRoomRepository.findBySenderIdAndReceiverId(senderId, receiverId)
                .orElseGet(() -> {  // 발신자 - 수신자가 최초 대화할 경우, 새로운 채팅방 엔티티 생성
                    Member sender = memberRepository.findById(senderId)
                            .orElseThrow(() -> new EntityNotFoundException("발신자 엔티티 조회 오류: [senderId=" + senderId + "]"));
                    authorizationMember(sender);
                    Member receiver = memberRepository.findById(receiverId)
                            .orElseThrow(() -> new EntityNotFoundException("수신자 엔티티 조회 오류: [receiverId=" + receiverId + "]"));

                    ChatRoom newChatRoom = ChatRoom.builder()
                            .roomId(UUID.randomUUID().toString())
                            .sender(sender)
                            .receiver(receiver)
                            .build();

                    log.info("ChatRoomService - createOrGetChatRoom(): ChatRoom Entity Create Success. id={}", newChatRoom.getId());
                    return chatRoomRepository.save(newChatRoom);
                });

        log.info("ChatRoomService - createOrGetChatRoom(): ChatRoom Entity Loading Success. id={}", chatRoom.getRoomId());
        return chatRoom;
    }

    @Comment("참여중인 채팅방 목록 전체 조회")
    @Transactional(readOnly = true)
    public List<ChatRoomResponse.getChatRoomList> getAllChatRooms(Long memberId) {
        log.info("ChatRoomService - getAllChatRooms() 호출 성공.");
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("ChatRoomService - getAllChatRooms() - 회원 엔티티 조회 실패. id=" + memberId));
        authorizationMember(member);

        List<Object[]> result = chatRoomRepository.findAllByMemberId(member.getId());

        return result.stream()
                .map(row -> new ChatRoomResponse.getChatRoomList(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        ((Number) row[2]).longValue(),
                        (String) row[3],
                        (String) row[4],
                        row[5] != null ? ((Timestamp) row[5]).toLocalDateTime() : null
                ))
                .collect(Collectors.toList());
    }

    @Comment("참여중인 특정 채팅방 조회")
    @Transactional(readOnly = true)
    public ChatRoomResponse.getChatRoomDetail getChatRoom(Long memberId, String roomId) {
        log.info("ChatRoomService - getChatRooms(): 호출 성공.");
        Member sender = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("ChatRoomService - getChatRooms: 회원 엔티티 조회 실패. id=" + memberId));
        authorizationMember(sender);

        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new EntityNotFoundException("ChatRoomService - getChatRooms(): 채팅방 엔티티 조회 실패. roomId=" + roomId));

        List<ChatMessage> chatMessages = chatMessageRepository.findAllByChatRoomOrderBySentAtAsc(chatRoom);

        // 수신자(회원) id 조회
        Long receiverId = null;
        String receiverName;
        if (sender.getId() != chatRoom.getReceiver().getId()) { // 고객 시점에서 특정 돌봄사와의 채팅방을 조회할 경우(ChatRoom 엔티티 기준 receiver를 수신자로 간주)
            receiverId = chatRoom.getReceiver().getId();
            receiverName = chatRoom.getReceiver().getName();
            return new ChatRoomResponse.getChatRoomDetail(chatRoom.getId(), chatRoom.getRoomId(), sender.getId(), receiverId, receiverName, chatMessages);
        }
        // 돌봄사 시점에서 특정 고객과의 채팅방을 조회할 경우(ChatRoom 엔티티 기준 발신자를 수신자로 간주)
        receiverId = chatRoom.getSender().getId();
        receiverName = chatRoom.getSender().getName();
        return new ChatRoomResponse.getChatRoomDetail(chatRoom.getId(), chatRoom.getRoomId(), sender.getId(), receiverId, receiverName, chatMessages);
    }

    @Comment("특정 돌봄사와의 진행중인 채팅방 존재 여부 확인")
    @Transactional(readOnly = true)
    public Optional<ChatRoomResponse.getExistsChatRoomDetail> existsChatRooms(Long senderId, Long receiverId) {
        log.info("ChatRoomService - existsChatRooms() 호출 성공.");
        return chatRoomRepository.existsChatRooms(senderId, receiverId)
                .map(cr -> new ChatRoomResponse.getExistsChatRoomDetail(cr.getRoomId(), cr.getReceiverId(), cr.getReceiverName()));
    }

    private static void authorizationMember(Member member) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName(); // 로그인에 사용된 아이디 값 반환

        if(!member.getLoginId().equals(userName)) {
            throw new IllegalArgumentException("회원 본인만 가능합니다.");
        }
    }
}
