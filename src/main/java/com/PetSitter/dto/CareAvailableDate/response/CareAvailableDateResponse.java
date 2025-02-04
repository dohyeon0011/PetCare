package com.PetSitter.dto.CareAvailableDate.response;

import com.PetSitter.domain.CareAvailableDate.CareAvailableDate;
import com.PetSitter.domain.CareAvailableDate.CareAvailableDateStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class CareAvailableDateResponse {

    @NoArgsConstructor
    @Getter
    public static class GetList {
        private long id;
        private LocalDate availableAt;
        private int price;
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
    public static class GetDetail {
        private long id;
        private LocalDate availableAt;
        private int price;
        private String zipcode;
        private String address;
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
    public static class GetReservation { // 고객이 예약 시 보여질 데이터
        private long id;
        private LocalDate availableAt;
        private int price;

        public GetReservation(CareAvailableDate careAvailableDate) {
            this.id = careAvailableDate.getId();
            this.availableAt = careAvailableDate.getAvailableAt();
            this.price = careAvailableDate.getPrice();
        }
    }

}
