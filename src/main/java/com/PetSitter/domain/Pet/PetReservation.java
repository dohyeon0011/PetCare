package com.PetSitter.domain.Pet;

import com.PetSitter.domain.Reservation.CustomerReservation.CustomerReservation;
import com.PetSitter.domain.Reservation.SitterSchedule.SitterSchedule;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PetReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pet_reservation_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
    @JsonIgnore
    private Pet pet;

    @Comment("고객 시점 예약")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_reservation_id")
    @JsonIgnore
    private CustomerReservation customerReservation;

    @Comment("돌봄사 시점 예약")
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "sitter_schedule_id")
    @JsonIgnore
    private SitterSchedule sitterSchedule;

    public static PetReservation createPetReservation(Pet pet) {
        PetReservation petReservation = new PetReservation();
        petReservation.addPet(pet);

        return petReservation;
    }

    public void addPet(Pet pet) {
        this.pet = pet;
    }

    public void addCustomerReservation(CustomerReservation customerReservation) {
        this.customerReservation = customerReservation;
    }

    public void addSitterSchedule(SitterSchedule sitterSchedule) {
        this.sitterSchedule = sitterSchedule;
    }

}
