package com.PetSitter.dto.Reservation.CustomerReservation.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddCustomerReservationRequest {
    @NotNull(message = "돌봄 예약하는 고객의 존재 여부는 필수입니다.")
    private long customerId; // 고객

    @NotNull(message = "돌봄을 맡길 돌봄사 선택은 필수입니다.")
    private long sitterId; // 돌봄사

    @NotNull(message = "날짜 선택은 필수입니다.")
    private long careAvailableId;

    @NotEmpty(message = "돌봄을 맡길 반려견 선택은 필수입니다.")
    private List<Long> petIds;

    private String requests;

    private int amount;
}
