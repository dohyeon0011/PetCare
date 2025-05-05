package com.PetSitter.dto.Reservation;

import com.PetSitter.domain.CareAvailableDate.CareAvailableDate;
import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Pet.Pet;
import com.PetSitter.dto.CareAvailableDate.response.CareAvailableDateResponse;
import com.PetSitter.dto.Pet.response.PetResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
@Schema(description = "고객 예약 시 보여질 폼 데이터 Response DTO")
public class ReservationResponse { // 고객이 예약할 때 보여줄 정보
    @Schema(description = "고객 id")
    private long customerId;

    @Schema(description = "돌봄사 id")
    private long sitterId;

    @Schema(description = "고객 닉네임(활동명)")
    private String customerNickName;

    @Schema(description = "돌봄사 이름(실명)")
    private String sitterName;

    @Schema(description = "돌봄 예약 가능 날짜")
    private List<CareAvailableDateResponse.GetReservation> careAvailableDates;

    @Schema(description = "돌봄 주소(우편번호)", pattern = "11111")
    private String zipcode;

    @Schema(description = "돌봄 주소(상세주소)")
    private String address;

    @Schema(description = "사용할 적립금")
    private int amount;

    @Schema(description = "돌봄에 맡길 반려견")
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
