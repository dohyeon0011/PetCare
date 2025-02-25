package com.PetSitter.dto.Reservation.CustomerReservation.response;

import com.PetSitter.domain.CareLog.CareLog;
import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Pet.PetReservation;
import com.PetSitter.domain.Reservation.CustomerReservation.CustomerReservation;
import com.PetSitter.domain.Reservation.CustomerReservation.ReservationStatus;
import com.PetSitter.domain.Review.Review;
import com.PetSitter.dto.CareLog.response.CareLogResponse;
import com.PetSitter.dto.Pet.response.PetReservationResponse;
import com.PetSitter.dto.Review.response.ReviewResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public class CustomerReservationResponse { // 고객 시점 예약 조회

    @NoArgsConstructor
    @Getter
    public static class GetList {
        private long id;
        private String sitterName;
        private LocalDate reservationAt;
        private LocalDateTime createdAt;
        private ReservationStatus status;

        public GetList(long id, String sitterName, LocalDate reservationAt, LocalDateTime createdAt, ReservationStatus status) {
            this.id = id;
            this.sitterName = sitterName;
            this.reservationAt = reservationAt;
            this.createdAt = createdAt;
            this.status = status;
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
        private String requests;
        private LocalDateTime createdAt;
        private ReservationStatus status;
        private List<PetReservationResponse> pets;
        private List<CareLogResponse.GetDetail> careLogList;
        private ReviewResponse.GetDetail review;

        public GetDetail(Member customer, Member sitter, CustomerReservation customerReservation, List<PetReservation> pets, List<CareLog> careLogList, Review review) {
            this.id = customerReservation.getId();
            this.customerId = customer.getId();
            this.customerNickName = customer.getNickName();
            this.sitterName = sitter.getName();
            this.price = customerReservation.getPrice();
            this.reservationAt = customerReservation.getReservationAt();
            this.zipcode = sitter.getZipcode();
            this.address = sitter.getAddress();
            this.requests = customerReservation.getRequests();
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
            // Optional.of()은 null 허용 X(value가 null이면 NPE 터짐. null이 아닐 때만 사용.)
            this.review = Optional.ofNullable(review)
                    .filter(r -> !r.isDeleted())
                    .map(r -> new ReviewResponse.GetDetail(
                            r.getId(), r.getCustomerReservation().getId(), r.getCustomerReservation().getCustomer().getNickName(),
                            r.getCustomerReservation().getSitter().getName(), r.getCustomerReservation().getReservationAt(), r.getRating(), r.getComment(), r.getCreatedAt()
                    ))
                    .orElse(new ReviewResponse.GetDetail());
        }
    }

}
