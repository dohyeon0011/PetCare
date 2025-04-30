package com.PetSitter.service.Admin.Member;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.MemberSearch;
import com.PetSitter.domain.Member.Role;
import com.PetSitter.dto.Member.response.AdminMemberResponse;
import com.PetSitter.repository.Member.MemberRepository;
import com.PetSitter.repository.Reservation.SitterSchedule.SitterScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AdminMemberService {

    private final MemberRepository memberRepository;
    private final SitterScheduleRepository sitterScheduleRepository;

    @Comment("관리자 페이지 모든 회원 목록 조회")
    @Transactional(readOnly = true)
    public Page<AdminMemberResponse.MemberListResponse> findAllForAdmin(Member member, MemberSearch memberSearch, Pageable pageable) {
        verifyingPermissionsAdmin(member);

        return memberRepository.findAllMember(memberSearch, pageable);
    }

    @Comment("관리자 페이지 회원 상세 정보 조회")
    @Transactional(readOnly = true)
    public Object findByIdForAdmin(long id, Member member) {
        verifyingPermissionsAdmin(member);

        Member findMember = memberRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));

        return findMember.toDetailResponseForAdmin();
    }

    @Comment("관리자 권한 회원 탈퇴")
    @Transactional
    public void deleteForAdmin(long id, Member member) {
        verifyingPermissionsAdmin(member);

        Member findMember = memberRepository.findByIdAndFalseWithLock(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));

        // 해당 회원이 돌봄사인데 돌봄 예약이 진행중인 건이 있을 때 예외 터트리기.
        if (findMember.getRole().equals(Role.PET_SITTER)) {
            if (!sitterScheduleRepository.findBySitterId(findMember.getId()).isEmpty()) {
                throw new IllegalArgumentException("회원 탈퇴 오류[id=" + id + "]: " + "현재 돌봄이 진행중인 예약이 존재합니다.");
            }
        }

        findMember.changeIsDeleted(true);
    }

    public static void verifyingPermissionsAdmin(Member member) {
        if (!member.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException("관리자 인증이 되지 않은 사용자입니다.");
        }
    }
}
