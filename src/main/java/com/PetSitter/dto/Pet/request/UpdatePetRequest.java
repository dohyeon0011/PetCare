package com.PetSitter.dto.Pet.request;

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
public class UpdatePetRequest {
    private long id;

    private String name;

    @NotNull(message = "반려견 나이 입력은 필수입니다.")
    private int age;

    @NotBlank(message = "반려견 품종 입력은 필수입니다.")
    private String breed;

    private String medicalConditions;

    private MultipartFile profileImage;
}
