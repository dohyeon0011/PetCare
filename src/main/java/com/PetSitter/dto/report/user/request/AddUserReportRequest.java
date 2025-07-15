package com.PetSitter.dto.report.user.request;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.report.user.UserReport;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Schema(description = "회원 신고 등록 Req DTO")
public class AddUserReportRequest {

    @Schema(description = "신고 당한 회원 id")
    private Long reportedUserId;

    @Schema(description = "신고 내용")
    private String content;

    public UserReport toEntity(Member reporter, Member reportedUser) {
        return UserReport.builder()
                .reporter(reporter)
                .reportedUser(reportedUser)
                .content(content)
                .build();
    }
}
