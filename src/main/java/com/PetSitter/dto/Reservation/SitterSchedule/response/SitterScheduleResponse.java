package com.PetSitter.dto.Reservation.SitterSchedule.response;

import com.PetSitter.domain.CareLog.CareLog;
import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Pet.PetReservation;
import com.PetSitter.domain.Reservation.CustomerReservation.ReservationStatus;
import com.PetSitter.domain.Reservation.SitterSchedule.SitterSchedule;
import com.PetSitter.dto.CareLog.response.CareLogResponse;
import com.PetSitter.dto.Pet.response.PetReservationResponse;
import com.PetSitter.dto.Review.response.ReviewResponse;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class SitterScheduleResponse { // 돌봄사 시점 예약 조회

    @NoArgsConstructor
    @Getter
    public static class GetList {
        private long id;
        private String customerNickName;
        private LocalDate reservationAt;
        private LocalDateTime createdAt;
        private ReservationStatus status;

        @QueryProjection
        public GetList(long id, String customerNickName, LocalDate reservationAt, LocalDateTime createdAt, ReservationStatus status) {
            this.id = id;
            this.customerNickName = customerNickName;
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

        public GetDetail(Member customer, Member sitter, SitterSchedule sitterSchedule, List<PetReservation> pets, List<CareLog> careLogList) {
            this.id = sitterSchedule.getId();
            this.customerId = customer.getId();
            this.customerNickName = customer.getNickName();
            this.sitterName = sitter.getName();
            this.price = sitterSchedule.getPrice();
            this.reservationAt = sitterSchedule.getReservationAt();
            this.zipcode = sitter.getZipcode();
            this.address = sitter.getAddress();
            this.requests = sitterSchedule.getRequests();
            this.createdAt = sitterSchedule.getCreatedAt();
            this.status = sitterSchedule.getStatus();
            this.pets = pets
                    .stream()
                    .map(PetReservationResponse::new)
                    .toList();
            this.careLogList = careLogList.stream()
                    .map(c -> {
                        return new CareLogResponse.GetDetail(c.getId(), c.getSitterSchedule().getSitter().getName(), c.getCareType(),
                                c.getDescription(), c.getImgPath(), c.getCreatedAt());
                    }).toList();
            this.review = Optional.ofNullable(sitterSchedule.getCustomerReservation().getReview())
                    .map(r -> {
                        return new ReviewResponse.GetDetail(r.getId(), r.getCustomerReservation().getId(), r.getCustomerReservation().getCustomer().getNickName(),
                                r.getCustomerReservation().getSitter().getName(), r.getRating(), r.getComment());
                    })
                    .orElse(new ReviewResponse.GetDetail());
        }

        /*public GetDetail(long id, long customerId, String customerNickName, long sitterId, String sitterName, int price, LocalDate reservationAt, String zipcode, String address, LocalDateTime createdAt, ReservationStatus status, List<PetReservation> pets, long reviewId, long customerReservationId, Double rating, String comment) {
            this.id = id;
            this.customerId = customerId;
            this.customerNickName = customerNickName;
            this.sitterId = sitterId;
            this.sitterName = sitterName;
            this.price = price;
            this.reservationAt = reservationAt;
            this.zipcode = zipcode;
            this.address = address;
            this.createdAt = createdAt;
            this.status = status;
            this.pets = pets
                    .stream()
                    .map(PetReservationResponse::new)
                    .toList();
            this.review = Optional.ofNullable(new ReviewResponse.GetDetail(reviewId, customerReservationId, customerNickName, sitterName, rating, comment))
                    .orElse(new ReviewResponse.GetDetail());
        }*/

        /*@QueryProjection
        public GetDetail(long id, long customerId, String customerNickName, long sitterId, String sitterName, int price, LocalDate reservationAt, String zipcode, String address, LocalDateTime createdAt, ReservationStatus status, List<PetReservation> pets, long reviewId, long customerReservationId, Double rating, String comment) {
            this.id = id;
            this.customerId = customerId;
            this.customerNickName = customerNickName;
            this.sitterId = sitterId;
            this.sitterName = sitterName;
            this.price = price;
            this.reservationAt = reservationAt;
            this.zipcode = zipcode;
            this.address = address;
            this.createdAt = createdAt;
            this.status = status;
            this.pets = pets
                    .stream()
                    .map(PetReservationResponse::new)
                    .toList();
            this.review = Optional.ofNullable(new ReviewResponse.GetDetail(reviewId, customerReservationId, customerNickName, sitterName, rating, comment))
                    .orElse(new ReviewResponse.GetDetail());
        }*/
    }

}
