package com.PetSitter.service.Admin.report.user;

import com.PetSitter.config.annotation.ReadOnlyTransactional;
import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.Role;
import com.PetSitter.domain.report.user.UserReport;
import com.PetSitter.dto.report.user.request.AdminUpdateUserReportRequest;
import com.PetSitter.dto.report.user.response.AdminUserReportResponse;
import com.PetSitter.repository.report.user.UserReportRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminUserReportService {

    private final UserReportRepository userReportRepository;

    /**
     * 관리자: 유저 신고 문의 내역 조회 메서드
     */
    @ReadOnlyTransactional
    public Page<AdminUserReportResponse.UserReportListDTO> indexUserReport(Member admin, Pageable pageable) {
        log.info("AdminUserReportService - indexUserReport(): Call Success.");
        verifyingPermissionsAdmin(admin);
        return userReportRepository.findAllUserReportDTOForAdmin(pageable);
    }

    /**
     * 관리자: 유저 신고 문의 상세 조회 메서드
     */
    @ReadOnlyTransactional
    public AdminUserReportResponse.UserReportDetailDTO showUserReport(Member admin, Long userReportId) {
        log.info("AdminUserReportService - showUserReport(): Call Success.");
        verifyingPermissionsAdmin(admin);
        return userReportRepository.findUserReportDetailDTOByIdForAdmin(userReportId);
    }

    /**
     * 관리자: 유저 신고 문의 처리 상태 수정 메서드
     */
    public void updateUserReport(Member admin, Long userReportId, AdminUpdateUserReportRequest request) {
        log.info("AdminUserReportService - updateUserReport(): Call Success.");
        verifyingPermissionsAdmin(admin);
        UserReport userReport = userReportRepository.findById(userReportId)
                .orElseThrow(() -> new EntityNotFoundException("유저 신고 문의 엔티티 조회에 실패했습니다. reportId={" + userReportId + "}"));

        userReport.changeReportStatus(request.getUpdateStatus());
        userReportRepository.saveAndFlush(userReport);
    }

    private static void verifyingPermissionsAdmin(Member admin) {
        if (!admin.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("관리자 인증이 되지 않은 사용자입니다.");
        }
    }
}
