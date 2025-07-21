package com.PetSitter.repository.report.user;

import com.PetSitter.domain.report.user.UserReport;
import com.PetSitter.dto.report.user.response.UserReportResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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
                ur.id, reporter.name, reportedUser.nickName, ur.title, ur.status, ur.createdAt
            )
            from UserReport ur
            join ur.reporter reporter
            join ur.reportedUser reportedUser
            where reporter.id = :memberId
            """)
    List<UserReportResponse.UserReportListDTO> findAllUserReportDTOByMemberId(@Param("memberId") Long memberId);

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
}
