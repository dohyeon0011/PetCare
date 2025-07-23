package com.PetSitter.dto.report.user.request;

import com.PetSitter.domain.report.user.ReportStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Schema(description = "관리자 - 유저 신고 문의 처리 상태 수정 Req DTO")
public class AdminUpdateUserReportRequest {

    @Schema(description = "유저 신고 문의 id")
    private Long id;

    @Schema(description = "수정할 처리 상태")
    private ReportStatus updateStatus;
}
