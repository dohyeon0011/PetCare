package com.PetSitter.dto.Review.response;

import com.PetSitter.domain.Member.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReviewResponse {

    @NoArgsConstructor
    @Getter
    public static class GetList { // 모든 리뷰 조회할 때
        private long id;
        private long customerReservationId;
        private String customerNickName;
        private String sitterName;
        private Double rating;
        private LocalDateTime createdAt;
        private Role role;

        public GetList(long reviewId, long customerReservationId, String nickName, String name, Double rating, LocalDateTime createdAt, Role role) {
            this.id = reviewId;
            this.customerReservationId = customerReservationId;
            this.customerNickName = nickName;
            this.sitterName = name;
            this.rating = rating;
            this.createdAt = createdAt;
            this.role = role;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class GetDetail { // 리뷰의 자세한 정보 조회
        private long id;
        private long customerReservationId;
        private String customerNickName;
        private String sitterName;
        private LocalDate reservationAt;
        private Double rating;
        private String comment;
        private LocalDateTime createdAt;

        /*public GetDetail(Review review) {
            this.id = review.getId();
            this.customerReservationId = review.getCustomerReservation().getId();
            this.customerNickName = review.getCustomerReservation().getCustomer().getNickName();
            this.sitterName = review.getCustomerReservation().getSitter().getName();
            this.rating = review.getRating();
            this.comment = review.getComment();
        }*/

        public GetDetail(long id, long customerReservationId, String customerNickName, String sitterName, LocalDate reservationAt, Double rating, String comment, LocalDateTime createdAt) {
            this.id = id;
            this.customerReservationId = customerReservationId;
            this.customerNickName = customerNickName;
            this.sitterName = sitterName;
            this.reservationAt = reservationAt;
            this.rating = rating;
            this.comment = comment;
            this.createdAt = createdAt;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class GetNewReview { // 리뷰 작성 시 보여질 폼 데이터
        private long customerReservationId;
        private String customerNickName;
        private String sitterName;
        private Double rating;
        private String comment;

        /*public GetNewReview(CustomerReservation customerReservation) {
            this.customerReservationId = customerReservation.getId();
            this.customerNickName = customerReservation.getCustomer().getNickName();
            this.sitterName = customerReservation.getSitter().getName();
        }*/

        public GetNewReview(long customerReservationId, String customerNickName, String sitterName) {
            this.customerReservationId = customerReservationId;
            this.customerNickName = customerNickName;
            this.sitterName = sitterName;
        }
    }

}
