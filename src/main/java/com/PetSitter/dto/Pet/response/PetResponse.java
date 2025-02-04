package com.PetSitter.dto.Pet.response;

import com.PetSitter.domain.Pet.Pet;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class PetResponse {

    @NoArgsConstructor
    @Getter
    public static class GetList {
        private long id;
        private String name;
        private int age;
        private String breed;
        private String medicalConditions;
        private String profileImgUrl;

        public GetList(Pet pet) {
            this.id = pet.getId();
            this.name = pet.getName();
            this.age = pet.getAge();
            this.breed = pet.getBreed();
            this.medicalConditions = pet.getMedicalConditions();
            this.profileImgUrl = pet.getProfileImgPath();
        }
    }

    @NoArgsConstructor
    @Getter
    public static class GetReservation { // 고객이 예약 시 보여질 반려견 정보
        private long id;
        private String name;
        private String breed;

        public GetReservation(Pet pet) {
            this.id = pet.getId();
            this.name = pet.getName();
            this.breed = pet.getBreed();
        }
    }

}
