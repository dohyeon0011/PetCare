package com.PetSitter.repository.report.user;

import com.PetSitter.domain.report.user.UserReport;
import com.PetSitter.dto.report.user.response.AdminUserReportResponse;
import com.PetSitter.dto.report.user.response.UserReportResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserReportRepository extends JpaRepository<UserReport, Long> {
    // 유저 신고 id로 신고 문의 엔티티 조회
    @Query("""
            select new com.PetSitter.dto.report.user.response.UserReportResponse$UserReportDetailDTO(
                ur.id, reporter.name, reportedUser.nickName, ur.title, ur.content, ur.status, ur.createdAt, ur.isDeleted
            )
            from UserReport ur
            join ur.reporter reporter
            join ur.reportedUser reportedUser
            where ur.id = :id
            """)
    Optional<UserReportResponse.UserReportDetailDTO> findUserReportDetailDTOById(@Param("id") Long id);

    // 자신의 유저 신고 내역 조회
    @Query("""
            select new com.PetSitter.dto.report.user.response.UserReportResponse$UserReportListDTO(
                ur.id, reporter.name, reportedUser.nickName, ur.title, ur.status, ur.isDeleted, ur.createdAt
            )
            from UserReport ur
            join ur.reporter reporter
            join ur.reportedUser reportedUser
            where reporter.id = :memberId
            """)
    Page<UserReportResponse.UserReportListDTO> findAllUserReportDTOByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    @Query("""
            select new com.PetSitter.dto.report.user.response.UserReportResponse$UserReportDetailDTO(
                ur.id, reporter.name, reportedUser.nickName, ur.title, ur.content, ur.status, ur.createdAt, ur.isDeleted
            )
            from UserReport ur
            join ur.reporter reporter
            join ur.reportedUser reportedUser
            where ur.id = :id and reporter.id = :memberId
            """)
    UserReportResponse.UserReportDetailDTO findUserReportDetailDTOByIdAndMemberId(@Param("id") Long id, @Param("memberId") Long memberId);

    // 관리자 - 유저 신고 문의 내역 조회
    @Query("""
            select new com.PetSitter.dto.report.user.response.AdminUserReportResponse$UserReportListDTO(
                ur.id, reporter.name, reportedUser.nickName, ur.title, ur.status, ur.isDeleted, ur.createdAt
            )
            from UserReport ur
            join ur.reporter reporter
            join ur.reportedUser reportedUser
            """)
    Page<AdminUserReportResponse.UserReportListDTO> findAllUserReportDTOForAdmin(Pageable pageable);

    // 관리자 - 유저 신고 문의 상세 조회
    @Query("""
            select new com.PetSitter.dto.report.user.response.AdminUserReportResponse$UserReportDetailDTO(
                ur.id, reporter.name, reportedUser.nickName, ur.title, ur.content, ur.status, ur.isDeleted, ur.createdAt
            )
            from UserReport ur
            join ur.reporter reporter
            join ur.reportedUser reportedUser
            where ur.id = :id
            """)
    AdminUserReportResponse.UserReportDetailDTO findUserReportDetailDTOByIdForAdmin(@Param("id") Long id);

    // 관리자 - 유저 신고 문의 답변 페이지 데이터 조회
    @Query("""
            select new com.PetSitter.dto.report.user.response.AdminUserReportResponse$AdminUserReportReplyResDTO(
                ur.id, reporter.name, reportedUser.nickName, ur.title, ur.content, ur.createdAt
            )
            from UserReport ur
            join ur.reporter reporter
            join ur.reportedUser reportedUser
            where ur.id = :id
            """)
    AdminUserReportResponse.AdminUserReportReplyResDTO findUserReportReplyResDTOForAdmin(@Param("id") Long id);
}
