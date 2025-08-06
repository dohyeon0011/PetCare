package com.PetSitter.dto.caretime.response;

import com.PetSitter.domain.CareAvailableDate.CareAvailableDateStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;


@Schema(description = "예약 가능 시간 정보 조회 Res DTO")
public class CareAvailableTimeResponse {

    @NoArgsConstructor
    @Getter
    @Schema(description = "예약 가능 시간 정보 상세 조회 Res DTO")
    public static class CareAvailableTimeDetailDTO {

        @Schema(description = "돌봄 예약 시간 id")
        private Long id;

        @Schema(description = "돌봄 예약 날짜 id")
        private Long careAvailableDateId;

        @Schema(description = "예약 시간")
        private LocalTime availableAt;

        @Schema(description = "돌봄 비용")
        private int price;

        @Schema(description = "예약 상태")
        private CareAvailableDateStatus status;

        public CareAvailableTimeDetailDTO(Long id, Long careAvailableDateId, LocalTime availableAt, int price, CareAvailableDateStatus status) {
            this.id = id;
            this.careAvailableDateId = careAvailableDateId;
            this.availableAt = availableAt;
            this.price = price;
            this.status = status;
        }
    }
}
