package com.PetSitter.dto.Pet.response;

import com.PetSitter.domain.Pet.PetReservation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Schema(description = "돌봄 예약 - 반려견 중간 매핑 반려견 정보 Response DTO")
public class PetReservationResponse { // 고객 시점 예약 엔티티 - 반려견 중간 매핑 엔티티

    @NoArgsConstructor
    @Getter
    @Schema(description = "돌봄 예약 - 반려견 중간 매핑 반려견 정보 조회 Response DTO")
    public static class PetDetailResponse {
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

        public PetDetailResponse(PetReservation petReservation) {
            this.name = petReservation.getPet().getName();
            this.age = petReservation.getPet().getAge();
            this.breed = petReservation.getPet().getBreed();
            this.medicalConditions = petReservation.getPet().getMedicalConditions();
            this.profileImage = petReservation.getPet().getProfileImage();
        }
    }
}
