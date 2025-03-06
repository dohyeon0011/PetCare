package com.PetSitter.service.Admin.PointHistory;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.Role;
import com.PetSitter.domain.PointHistory.PointSearch;
import com.PetSitter.dto.PointHistory.response.AdminPointHistoryResponse;
import com.PetSitter.repository.PointHistory.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminPointHistoryService {

    private final PointHistoryRepository pointHistoryRepository;

    @Comment("관리자 페이지 회원 포인트 내역 전체 조회")
    @Transactional(readOnly = true)
    public Page<AdminPointHistoryResponse.PointListResponse> findAllForAdmin(PointSearch pointSearch, Member member, Pageable pageable) {
        verifyingPermissionsAdmin(member);

        return pointHistoryRepository.findAll(pointSearch, pageable);
    }

    public static void verifyingPermissionsAdmin(Member member) {
        if (!member.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException("관리자 인증이 되지 않은 사용자입니다.");
        }
    }
}
