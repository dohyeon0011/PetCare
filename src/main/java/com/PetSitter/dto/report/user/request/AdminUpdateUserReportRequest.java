package com.PetSitter.dto.report.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Schema(description = "관리자 - 유저 신고 문의 답변 등록 Req DTO")
public class AdminUpdateUserReportRequest {

    @Schema(description = "관리자 문의 답변 내용")
    @NotBlank(message = "답변 내용을 필수로 입력해야 합니다.")
    private String replyContent;
}
