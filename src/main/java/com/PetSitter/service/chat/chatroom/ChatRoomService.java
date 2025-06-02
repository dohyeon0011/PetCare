package com.PetSitter.service.chat.chatroom;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.chat.ChatRoom;
import com.PetSitter.dto.chat.chatroom.response.ChatRoomResponse;
import com.PetSitter.repository.Member.MemberRepository;
import com.PetSitter.repository.chat.chatroom.ChatRoomRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

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

                    log.info("ChatRoomService: createOrGetChatRoom() - ChatRoom Entity Create Success. id={}", newChatRoom.getId());
                    return chatRoomRepository.save(newChatRoom);
                });

        log.info("ChatRoomService: createOrGetChatRoom() - ChatRoom Entity Loading Success. id={}", chatRoom.getRoomId());
        return chatRoom;
    }

    @Comment("참여중인 채팅방 목록 전체 조회")
    public List<ChatRoomResponse.getChatRoomList> getAllChatRooms(Long memberId) {
        log.info("ChatRoomService - getAllChatRooms() 호출 성공.");
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("ChatRoomService: getAllChatRooms() - 회원 엔티티 조회 실패. id=" + memberId));
        authorizationMember(member);

        return chatRoomRepository.findAllByMemberId(member.getId());
    }

    private static void authorizationMember(Member member) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName(); // 로그인에 사용된 아이디 값 반환

        if(!member.getLoginId().equals(userName)) {
            throw new IllegalArgumentException("회원 본인만 가능합니다.");
        }
    }
}
