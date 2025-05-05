package com.PetSitter.dto.Certification.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Schema(description = "자격증 정보 수정 Req DTO")
public class UpdateCertificationRequest {

    @Schema(description = "자격증 id")
    private long id;

    @Schema(description = "수정할 자격증 이름")
    @NotBlank(message = "자격증 이름 입력은 필수입니다.")
    private String name;

}
