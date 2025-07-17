package com.PetSitter.dto.report.user.request;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.report.user.UserReport;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Schema(description = "회원 신고 등록 Req DTO")
public class AddUserReportRequest {

    @Schema(description = "신고 당한 회원 id")
    @NotNull(message = "신고 대상 회원 id 값은 필수입니다.")
    private Long reportedUserId;

    @Schema(description = "신고 문의 제목")
    @NotBlank(message = "신고 문의 제목 필드 입력은 필수입니다.")
    private String title;

    @Schema(description = "신고 내용")
    @NotBlank(message = "신고 내용 필드 입력은 필수입니다.")
    private String content;

    public UserReport toEntity(Member reporter, Member reportedUser) {
        return UserReport.builder()
                .reporter(reporter)
                .reportedUser(reportedUser)
                .title(title)
                .content(content)
                .build();
    }
}
