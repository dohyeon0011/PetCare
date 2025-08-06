package com.PetSitter.dto.caretime.request;

import com.PetSitter.domain.CareAvailableDate.CareAvailableDate;
import com.PetSitter.domain.caretime.CareAvailableTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Schema(description = "예약 가능 시간 등록 Req DTO")
@NoArgsConstructor
@Getter
public class AddCareAvailableTimeRequest {

    @Schema(description = "예약 가능 날짜 id")
    private Long careAvailableDateId;

    @Schema(description = "예약 가능 시간")
    private LocalTime availableAt;

    @Schema(description = "돌봄 비용")
    private int price;

    public CareAvailableTime toEntity(CareAvailableDate careAvailableDate) {
        return CareAvailableTime.builder()
                .careAvailableDate(careAvailableDate)
                .availableAt(availableAt)
                .price(price)
                .build();
    }
}
