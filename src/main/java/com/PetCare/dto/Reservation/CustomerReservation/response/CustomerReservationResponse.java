package com.PetCare.dto.Reservation.CustomerReservation.response;

import com.PetCare.domain.CareLog.CareLog;
import com.PetCare.domain.Member.Member;
import com.PetCare.domain.Pet.PetReservation;
import com.PetCare.domain.Reservation.CustomerReservation.CustomerReservation;
import com.PetCare.domain.Reservation.CustomerReservation.ReservationStatus;
import com.PetCare.dto.CareLog.response.CareLogResponse;
import com.PetCare.dto.Pet.response.PetReservationResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public class CustomerReservationResponse { // 고객 시점 예약 조회

    @NoArgsConstructor
    @Getter
    public static class GetList {
        private long id;
        private LocalDate reservationAt;
        private LocalDateTime createdAt;
        private ReservationStatus status;

        public GetList(CustomerReservation customerReservation) {
            this.id = customerReservation.getId();
            this.reservationAt = customerReservation.getReservationAt();
            this.createdAt = customerReservation.getCreatedAt();
            this.status = customerReservation.getStatus();
        }
    }

    @NoArgsConstructor
    @Getter
    public static class GetDetail {
        private long id;
        private long customerId;
        private String customerNickName;
        private String sitterName;
        private int price;
        private LocalDate reservationAt;
        private String zipcode;
        private String address;
        private LocalDateTime createdAt;
        private ReservationStatus status;
        private List<PetReservationResponse> pets;
        private List<CareLogResponse.GetDetail> careLogList;

        public GetDetail(Member customer, Member sitter, CustomerReservation customerReservation, List<PetReservation> pets, List<CareLog> careLogList) {
            this.id = customerReservation.getId();
            this.customerId = customer.getId();
            this.customerNickName = customer.getNickName();
            this.sitterName = sitter.getName();
            this.price = customerReservation.getPrice();
            this.reservationAt = customerReservation.getReservationAt();
            this.zipcode = sitter.getZipcode();
            this.address = sitter.getAddress();
            this.createdAt = customerReservation.getCreatedAt();
            this.status = customerReservation.getStatus();
            this.pets = pets
                    .stream()
                    .map(PetReservationResponse::new)
                    .toList();
            /*this.careLogList = careLogList
                    .stream()
                    .map(CareLogResponse.GetDetail::new)
                    .toList();*/
            this.careLogList = careLogList
                    .stream()
                    .map(careLog -> new CareLogResponse.GetDetail(
                            careLog.getId(),
                            careLog.getSitterSchedule().getSitter().getName(),
                            careLog.getCareType(),
                            careLog.getDescription(),
                            careLog.getImgPath(),
                            careLog.getCreatedAt()
                    ))
                    .toList();
        }
    }

}
