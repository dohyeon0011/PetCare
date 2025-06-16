package com.PetSitter.dto.Review.response;

import com.PetSitter.domain.Member.Role;
import com.PetSitter.domain.Review.Review;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "돌봄 리뷰 조회 Response DTO")
public class ReviewResponse {

    @NoArgsConstructor
    @Getter
    @Schema(description = "돌봄 리뷰 리스트 조회 Response DTO")
    public static class GetList { // 모든 리뷰 조회할 때
        @Schema(description = "리뷰 id")
        private long id;

        @Schema(description = "예약 id")
        private long customerReservationId;

        @Schema(description = "고객 닉네임(활동명)")
        private String customerNickName;

        @Schema(description = "돌봄사 이름(실명)")
        private String sitterName;

        @Schema(description = "돌봄 평점", minLength = 0, maxLength = 5, defaultValue = "0")
        private Double rating;

        @Schema(description = "작성일자", pattern = "yyyy-MM-dd:HH:mm:ss")
        private LocalDateTime createdAt;

        @Schema(description = "회원 유형", pattern = "CUSTOMER(고객), PET_SITTER(돌봄사)")
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
    @Schema(description = "돌봄 리뷰 상세 조회 Response DTO")
    public static class GetDetail { // 리뷰의 자세한 정보 조회
        @Schema(description = "리뷰 id")
        private long id;

        @Schema(description = "예약 id")
        private long customerReservationId;

        @Schema(description = "고객 닉네임(활동명)")
        private String customerNickName;

        @Schema(description = "돌봄사 이름(실명)")
        private String sitterName;

        @Schema(description = "돌봄 예약 날짜", pattern = "yyyy-MM-dd")
        private LocalDate reservationAt;

        @Schema(description = "돌봄 평점", minLength = 0, maxLength = 5, defaultValue = "0")
        private Double rating;

        @Schema(description = "돌봄 리뷰 내용")
        private String comment;

        @Schema(description = "작성일자", pattern = "yyyy-MM-dd:HH:mm:ss")
        private LocalDateTime createdAt;

        public GetDetail(Review review) {
            this.id = review.getId();
            this.customerReservationId = review.getCustomerReservation().getId();
            this.customerNickName = review.getCustomerReservation().getCustomer().getNickName();
            this.sitterName = review.getCustomerReservation().getSitter().getName();
            this.rating = review.getRating();
            this.comment = review.getComment();
        }

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
    @Schema(description = "돌봄 리뷰 작성 시 보여질 폼 데이터 Response DTO")
    public static class GetNewReview { // 리뷰 작성 시 보여질 폼 데이터
        @Schema(description = "예약 id")
        private long customerReservationId;

        @Schema(description = "고객 닉네임(활동명)")
        private String customerNickName;

        @Schema(description = "돌봄사 이름(실명)")
        private String sitterName;

        @Schema(description = "돌봄 평점", minLength = 0, maxLength = 5, defaultValue = "0")
        private Double rating;

        @Schema(description = "돌봄 리뷰 내용")
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
