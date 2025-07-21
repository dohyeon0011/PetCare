package com.PetSitter.service.report.user;

import com.PetSitter.config.annotation.ReadOnlyTransactional;
import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.report.user.UserReport;
import com.PetSitter.dto.report.user.request.AddUserReportRequest;
import com.PetSitter.dto.report.user.response.UserReportResponse;
import com.PetSitter.repository.Member.MemberRepository;
import com.PetSitter.repository.report.user.UserReportRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserReportService {

    private final UserReportRepository userReportRepository;
    private final MemberRepository memberRepository;

    /**
     * 유저 신고 문의 등록 메서드
     */
    @Transactional
    public UserReportResponse.UserReportDetailDTO insertUserReport(Member reporter, AddUserReportRequest request) {
        log.info("UserReportService - insertUserReport(): Call Success.");
        authorizationMember(reporter);
        Member reportedUser = memberRepository.findByIdAndFalseWithLock(request.getReportedUserId())
                .orElseThrow(() -> new EntityNotFoundException("신고 대상 엔티티 조회에 실패했습니다. reportedUserId={" + request.getReportedUserId() + "}"));

        UserReport userReport = request.toEntity(reporter, reportedUser);
        userReportRepository.saveAndFlush(userReport);

        return userReportRepository.findUserReportDetailDTOById(userReport.getId())
                .orElseThrow(() -> new EntityNotFoundException("유저 신고 엔티티 조회에 실패했습니다."));
    }

    /**
     * 유저 신고 문의 내역 조회 메서드
     */
    @ReadOnlyTransactional
    public List<UserReportResponse.UserReportListDTO> indexUserReport(Member reporter) {
        log.info("UserReportService - indexUserReport(): Call Success.");
        authorizationMember(reporter);

        return userReportRepository.findAllUserReportDTOByMemberId(reporter.getId());
    }

    /**
     * 유저 신고 문의 내역 상세 조회 메서드
     */
    @ReadOnlyTransactional
    public UserReportResponse.UserReportDetailDTO showUserReport(Long id, Member reporter) {
        log.info("UserReportService - showUserReport(): Call Success.");
        authorizationMember(reporter);

        return userReportRepository.findUserReportDetailDTOByIdAndMemberId(id, reporter.getId());
    }

    /**
     * 유저 신고 문의 내역 삭제 메서드
     */
    public void deleteUserReport(Long id, Member reporter) {
        log.info("UserReportService - deleteUserReport(): Call Success.");
        authorizationMember(reporter);

        UserReport userReport = userReportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("유저 신고 엔티티 조회에 실패했습니다. id={" + id + "}"));
        userReport.changeIsDelete();
        userReportRepository.saveAndFlush(userReport);
    }

    private static void authorizationMember(Member member) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!member.getLoginId().equals(username)) {
            throw new AccessDeniedException("회원 본인만 유저 신고 문의 등록이 가능합니다.");
        }
    }
}
