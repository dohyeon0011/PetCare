package com.PetSitter.dto.Pet.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@Getter
@Setter
@Schema(description = "반려견 수정 Req DTO")
public class UpdatePetRequest {
    @Schema(description = "반려견 id")
    private long id;

    @Schema(description = "수정할 반려견 이름")
    private String name;

    @Schema(description = "수정할 반려견 나이")
    @NotNull(message = "반려견 나이 입력은 필수입니다.")
    private int age;

    @Schema(description = "수정할 반려견 품종")
    @NotBlank(message = "반려견 품종 입력은 필수입니다.")
    private String breed;

    @Schema(description = "수정할 반려견 특이 사항", pattern = "건강 상태 및 특이사항")
    private String medicalConditions;

    @Schema(description = "수정할 반려견 프로필 이미지")
    private MultipartFile profileImage;
}
