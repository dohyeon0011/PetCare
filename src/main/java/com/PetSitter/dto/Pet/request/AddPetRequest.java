package com.PetSitter.dto.Pet.request;

import com.PetSitter.domain.Pet.Pet;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddPetRequest {
    private String name;

    @NotNull(message = "반려견 나이 입력은 필수입니다.")
    private int age;

    @NotBlank(message = "반려견 품종 입력은 필수입니다.")
    private String breed;

    private String medicalConditions;

    private String profileImgPath;

    public Pet toEntity() {
        return Pet.builder()
                .name(name)
                .age(age)
                .breed(breed)
                .medicalConditions(medicalConditions)
                .profileImgPath(profileImgPath)
                .build();
    }

}