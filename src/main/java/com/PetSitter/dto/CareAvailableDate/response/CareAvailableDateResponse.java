package com.PetSitter.dto.CareAvailableDate.response;

import com.PetSitter.domain.CareAvailableDate.CareAvailableDate;
import com.PetSitter.domain.CareAvailableDate.CareAvailableDateStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Schema(description = "등록한 돌봄 일정 조회 Response DTO")
public class CareAvailableDateResponse {

    @NoArgsConstructor
    @Getter
    @Schema(description = "등록한 돌봄 일정 리스트 조회 Response DTO")
    public static class GetList {
        @Schema(description = "돌봄 가능 날짜 id")
        private long id;

        @Schema(description = "등록한 돌봄 일정", pattern = "yyyy-MM-dd")
        private LocalDate availableAt;

        @Schema(description = "돌봄 비용")
        private int price;

        @Schema(description = "돌봄 가능 상태", pattern = "POSSIBILITY(예약 가능), IMPOSSIBILITY(예약 불가능)")
        private CareAvailableDateStatus status;

        public GetList(CareAvailableDate careAvailableDate) {
            this.id = careAvailableDate.getId();
            this.availableAt = careAvailableDate.getAvailableAt();
            this.price = careAvailableDate.getPrice();
            this.status = careAvailableDate.getStatus();
        }

        public GetList(Long id, LocalDate availableAt, int price, CareAvailableDateStatus status) {
            this.id = id;
            this.availableAt = availableAt;
            this.price = price;
            this.status = status;
        }
    }

    @NoArgsConstructor
    @Getter
    @Schema(description = "등록한 돌봄 일정 상세 조회 Response DTO")
    public static class GetDetail {
        @Schema(description = "돌봄 가능 날짜 id")
        private long id;

        @Schema(description = "등록한 돌봄 일정", pattern = "yyyy-MM-dd")
        private LocalDate availableAt;

        @Schema(description = "돌봄 비용")
        private int price;

        @Schema(description = "돌봄 주소(우편번호)", pattern = "12345")
        private String zipcode;

        @Schema(description = "돌봄 주소(상세주소)", pattern = "서울특별시 용산구 한남동")
        private String address;

        @Schema(description = "돌봄 가능 상태", pattern = "POSSIBILITY(예약 가능), IMPOSSIBILITY(예약 불가능)")
        private CareAvailableDateStatus status;

        public GetDetail(long id, LocalDate availableAt, int price, String zipcode, String address, CareAvailableDateStatus status) {
            this.id = id;
            this.availableAt = availableAt;
            this.price = price;
            this.zipcode = zipcode;
            this.address = address;
            this.status = status;
        }
    }

    @NoArgsConstructor
    @Getter
    @Schema(description = "고객이 예약 시 보여질 돌봄 날짜 관련 데이터 Response DTO")
    public static class GetReservation { // 고객이 예약 시 보여질 데이터
        @Schema(description = "돌봄 가능 날짜 id")
        private long id;

        @Schema(description = "등록한 돌봄 일정", pattern = "yyyy-MM-dd")
        private LocalDate availableAt;

        @Schema(description = "돌봄 비용")
        private int price;

        public GetReservation(CareAvailableDate careAvailableDate) {
            this.id = careAvailableDate.getId();
            this.availableAt = careAvailableDate.getAvailableAt();
            this.price = careAvailableDate.getPrice();
        }
    }

}
