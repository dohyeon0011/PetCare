package com.PetSitter.dto.Reservation.CustomerReservation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Schema(description = "돌봄 예약 Req DTO")
public class AddCustomerReservationRequest {
    @Schema(description = "고객 id")
    @NotNull(message = "돌봄 예약하는 고객의 존재 여부는 필수입니다.")
    private long customerId; // 고객

    @Schema(description = "돌봄사 id")
    @NotNull(message = "돌봄을 맡길 돌봄사 선택은 필수입니다.")
    private long sitterId; // 돌봄사

    @Schema(description = "돌봄 일정 id")
    @NotNull(message = "날짜 선택은 필수입니다.")
    private long careAvailableId;

    @Schema(description = "돌봄에 맡길 반려견", pattern = "ex) [1, 2]")
    @NotEmpty(message = "돌봄을 맡길 반려견 선택은 필수입니다.")
    private List<Long> petIds;

    @Schema(description = "고객 요청사항")
    private String requests;

    @Schema(description = "고객이 사용한 적립금")
    private int amount;
}
