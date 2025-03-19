package com.PetSitter.dto.Pet.response;

import com.PetSitter.domain.Pet.PetReservation;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class PetReservationResponse { // 고객 시점 예약 엔티티 - 반려견 중간 매핑 엔티티

    @NoArgsConstructor
    @Getter
    public static class PetDetailResponse {
        private String name;
        private int age;
        private String breed;
        private String medicalConditions;
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
