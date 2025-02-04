package com.PetSitter.dto.Certification.request;

import com.PetSitter.domain.Certification.Certification;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddCertificationRequest {

    @NotBlank(message = "자격증 이름 입력은 필수입니다.")
    private String name;

    public Certification toEntity() {
        return Certification.builder()
                .name(name)
                .build();
    }

}
