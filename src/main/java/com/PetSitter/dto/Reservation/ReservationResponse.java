package com.PetSitter.dto.Reservation;

import com.PetSitter.domain.CareAvailableDate.CareAvailableDate;
import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Pet.Pet;
import com.PetSitter.dto.CareAvailableDate.response.CareAvailableDateResponse;
import com.PetSitter.dto.Pet.response.PetResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class ReservationResponse { // 고객이 예약할 때 보여줄 정보
    private long customerId;
    private long sitterId;
    private String customerNickName;
    private String sitterName;
    private List<CareAvailableDateResponse.GetReservation> careAvailableDates;
    private String zipcode;
    private String address;
    private int amount;
    private List<PetResponse.GetReservation> pets;

    public ReservationResponse(Member customer, Member sitter, List<CareAvailableDate> careAvailableDates, List<Pet> pets) {
        this.customerId = customer.getId();
        this.sitterId = sitter.getId();
        this.customerNickName = customer.getNickName();
        this.sitterName = sitter.getName();
        this.zipcode = sitter.getZipcode();
        this.address = sitter.getAddress();
        this.amount = customer.getAmount();
        this.careAvailableDates = careAvailableDates
                .stream()
                .map(CareAvailableDateResponse.GetReservation::new)
                .toList();
        this.pets = pets
                .stream()
                .map(PetResponse.GetReservation::new)
                .toList();
    }

}
