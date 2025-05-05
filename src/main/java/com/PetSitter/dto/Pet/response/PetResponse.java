package com.PetSitter.dto.Pet.response;

import com.PetSitter.domain.Pet.Pet;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Schema(description = "반려견 정보 조회 Response DTO")
public class PetResponse {

    @NoArgsConstructor
    @Getter
    @Schema(description = "반려견 정보 리스트 조회 Response DTO")
    public static class GetList {
        @Schema(description = "반려견 id")
        private long id;

        @Schema(description = "반려견 이름")
        private String name;

        @Schema(description = "반려견 나이")
        private int age;

        @Schema(description = "반려견 품종")
        private String breed;

        @Schema(description = "반려견 특이사항", pattern = "건강 상태 및 특이사항")
        private String medicalConditions;

        @Schema(description = "반려견 프로필 이미지")
        private String profileImage;

        public GetList(Pet pet) {
            this.id = pet.getId();
            this.name = pet.getName();
            this.age = pet.getAge();
            this.breed = pet.getBreed();
            this.medicalConditions = pet.getMedicalConditions();
            this.profileImage = pet.getProfileImage();
        }
    }

    @NoArgsConstructor
    @Getter
    @Schema(description = "고객 예약 시 보여질 반려견 정보 조회 Response DTO")
    public static class GetReservation { // 고객이 예약 시 보여질 반려견 정보
        @Schema(description = "반려견 id")
        private long id;

        @Schema(description = "반려견 이름")
        private String name;

        @Schema(description = "반려견 나이")
        private int age;

        @Schema(description = "반려견 품종")
        private String breed;

        public GetReservation(Pet pet) {
            this.id = pet.getId();
            this.name = pet.getName();
            this.age = pet.getAge();
            this.breed = pet.getBreed();
        }
    }
}
