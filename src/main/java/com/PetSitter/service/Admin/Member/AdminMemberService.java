package com.PetSitter.service.Admin.Member;

import com.PetSitter.config.annotation.ReadOnlyTransactional;
import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.MemberSearch;
import com.PetSitter.domain.Member.Role;
import com.PetSitter.dto.Member.response.AdminMemberResponse;
import com.PetSitter.repository.Member.MemberRepository;
import com.PetSitter.repository.Reservation.SitterSchedule.SitterScheduleRepository;
import lombok.RequiredArgsConstructor;
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

    /**
     * 관리자 페이지 모든 회원 목록 조회
     * @ReadOnlyTransactional: 커스텀 읽기 전용 어노테이션
     */
    @ReadOnlyTransactional
    public Page<AdminMemberResponse.MemberListResponse> findAllForAdmin(Member admin, MemberSearch memberSearch, Pageable pageable) {
        verifyingPermissionsAdmin(admin);
        return memberRepository.findAllMember(memberSearch, pageable);
    }

    /**
     * 관리자 페이지 회원 상세 정보 조회
     * @ReadOnlyTransactional: 커스텀 읽기 전용 어노테이션
     */
    @ReadOnlyTransactional
    public Object findByIdForAdmin(long id, Member admin) {
        verifyingPermissionsAdmin(admin);

        Role findRole = memberRepository.findRoleById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다. id=" + id));
        if (findRole.equals(Role.CUSTOMER)) {
            Member findMember = memberRepository.findByCustomerIdAndRole(id, findRole)
                    .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다. id=" + id));
            return findMember.toDetailResponseForAdmin();
        } else if (findRole.equals(Role.PET_SITTER)) {
            Member findMember = memberRepository.findBySitterIdAndRole(id, findRole)
                    .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다. id=" + id));
            return findMember.toDetailResponseForAdmin();
        }

        throw new NoSuchElementException("존재하지 않는 회원입니다. id=" + id);
    }

    /**
     * 관리자 권한 회원 탈퇴
     */
    @Transactional
    public void deleteForAdmin(long id, Member admin) {
        verifyingPermissionsAdmin(admin);
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

    public static void verifyingPermissionsAdmin(Member admin) {
        if (!admin.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException("관리자 인증이 되지 않은 사용자입니다.");
        }
    }
}
