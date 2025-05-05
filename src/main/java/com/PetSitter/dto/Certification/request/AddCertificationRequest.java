package com.PetSitter.dto.Certification.request;

import com.PetSitter.domain.Certification.Certification;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Schema(description = "돌봄사 자격증 등록 Req DTO")
public class AddCertificationRequest {

    @Schema(description = "자격증 이름")
    @NotBlank(message = "자격증 이름 입력은 필수입니다.")
    private String name;

    public Certification toEntity() {
        return Certification.builder()
                .name(name)
                .build();
    }
}
