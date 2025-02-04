package com.PetSitter.domain.Reservation.SitterSchedule;//package com.PetCare.domain.SitterSchedule;
//
//import com.PetCare.domain.Pet.Pet;
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import jakarta.persistence.*;
//import lombok.AccessLevel;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//@Entity
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@Getter
//public class SitterPetReservation { // 돌봄해야 할 반려견(Pet)과 돌봄사 시점 예약 테이블(SitterSchedule) 중간 매핑 테이블
//
//    @Id @GeneratedValue
//    @Column(name = "sitter_pet_reservation_id")
//    private long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "sitter_schedule_id")
//    @JsonIgnore
//    private SitterSchedule sitterSchedule;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "pet_id")
//    @JsonIgnore
//    private Pet pet;
//
//    public static SitterPetReservation createSitterPetReservation(Pet pet) {
//        SitterPetReservation sitterPetReservation = new SitterPetReservation();
//        sitterPetReservation.addPet(pet);
//
//        return sitterPetReservation;
//    }
//
//    public void addSitterSchedule(SitterSchedule sitterSchedule) {
//        this.sitterSchedule = sitterSchedule;
//    }
//
//    public void addPet(Pet pet) {
//        this.pet = pet;
//    }
//
//}
