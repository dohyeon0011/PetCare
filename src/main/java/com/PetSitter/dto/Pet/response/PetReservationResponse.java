package com.PetSitter.dto.Pet.response;

import com.PetSitter.domain.Pet.PetReservation;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PetReservationResponse { // 고객 시점 예약 엔티티 - 반려견 중간 매핑 엔티티
    private String name;
    private int age;
    private String breed;

    public PetReservationResponse(PetReservation petReservation) {
        this.name = petReservation.getPet().getName();
        this.age = petReservation.getPet().getAge();
        this.breed = petReservation.getPet().getBreed();
    }
}
