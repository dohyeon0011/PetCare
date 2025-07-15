package com.PetSitter.dto.report.user.response;

import com.PetSitter.domain.report.user.ReportStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "회원 신고 정보 Res DTO")
public class UserReportResponse {

    @NoArgsConstructor
    @Getter
    @Schema(description = "회원 신고 정보 목록 조회 Res DTO")
    public static class UserReportListDTO {

        @Schema(description = "신고 문의 id")
        private Long id;

        @Schema(description = "신고한 회원")
        private String reporterName;

        @Schema(description = "신고 당한 회원")
        private String reportedUserName;

        @Schema(description = "신고 문의 처리 상태")
        private ReportStatus status;

        @Schema(description = "작성 시간")
        private LocalDateTime createdAt;

        public UserReportListDTO(Long id, String reporterName, String reportedUserName, ReportStatus status, LocalDateTime createdAt) {
            this.id = id;
            this.reporterName = reporterName;
            this.reportedUserName = reportedUserName;
            this.status = status;
            this.createdAt = createdAt;
        }
    }

    @NoArgsConstructor
    @Getter
    @Schema(description = "회원 신고 정보 상세 조회 Res DTO")
    public static class UserReportDetailDTO {
        @Schema(description = "신고 문의 id")
        private Long id;

        @Schema(description = "신고한 회원")
        private String reporterName;

        @Schema(description = "신고 당한 회원")
        private String reportedUserName;

        @Schema(description = "신고 내용")
        private String content;

        @Schema(description = "신고 문의 처리 상태")
        private ReportStatus status;

        @Schema(description = "작성 시간")
        private LocalDateTime createdAt;

        @Schema(description = "삭제 여부", pattern = "True = 삭제, False = 존재")
        @JsonProperty("isDeleted")
        private Boolean isDeleted;

        public UserReportDetailDTO(Long id, String reporterName, String reportedUserName, String content, ReportStatus status, LocalDateTime createdAt, Boolean isDeleted) {
            this.id = id;
            this.reporterName = reporterName;
            this.reportedUserName = reportedUserName;
            this.content = content;
            this.status = status;
            this.createdAt = createdAt;
            this.isDeleted = isDeleted;
        }
    }
}
