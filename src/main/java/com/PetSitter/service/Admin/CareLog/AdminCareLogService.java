package com.PetSitter.service.Admin.CareLog;

import com.PetSitter.domain.CareLog.CareLog;
import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.Role;
import com.PetSitter.repository.CareLog.CareLogRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AdminCareLogService {

    private final CareLogRepository careLogRepository;

    @Comment("관리자 권한 돌봄 케어 로그 삭제")
    @Transactional
    public void deleteForAdmin(long id, Member member) {
        verifyingPermissionsAdmin(member);

        CareLog careLog = careLogRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 돌봄 케어 로그입니다."));

        careLog.changeIsDeleted(true);
    }

    public static void verifyingPermissionsAdmin(Member member) {
        if (!member.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException("관리자 인증이 되지 않은 사용자입니다.");
        }
    }

}
