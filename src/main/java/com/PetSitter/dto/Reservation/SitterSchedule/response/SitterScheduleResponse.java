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
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Schema(description = "돌봄사 시점 - 돌봄 예약 정보 조회 Response DTO")
public class SitterScheduleResponse { // 돌봄사 시점 예약 조회

    @NoArgsConstructor
    @Getter
    @Schema(description = "돌봄사 시점 - 돌봄 예약 리스트 조회 Response DTO")
    public static class GetList {
        @Schema(description = "예약 id")
        private long id;

        @Schema(description = "고객 닉네임(활동명)")
        private String customerNickName;

        @Schema(description = "돌봄 예약 날짜", pattern = "yyyy-MM-dd")
        private LocalDate reservationAt;

        @Schema(description = "돌봄 발생 시간", pattern = "yyyy-MM-dd:HH:mm:ss")
        private LocalDateTime createdAt;

        @Schema(description = "예약 상태", pattern = "RESERVATION(예약완료), CANCEL(취소)")
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
    @Schema(description = "돌봄사 시점 - 돌봄 예약 상세 조회 Response DTO")
    public static class GetDetail {
        @Schema(description = "예약 id")
        private long id;

        @Schema(description = "고객 id")
        private long customerId;

        @Schema(description = "고객 닉네임(활동명)")
        private String customerNickName;

        @Schema(description = "돌봄사 이름(실명)")
        private String sitterName;

        @Schema(description = "고객이 사용한 적립금")
        private Integer usingPoint; // 고객이 사용한 포인트

        @Schema(description = "돌봄 예약 원금")
        private int originalPrice;  // 원래 돌봄 예약 가격

        @Schema(description = "최종 돌봄 예약 가격")
        private int resultPrice;    // 최종 가격(원래 돌봄 가격 - 고객 사용 포인트)

        @Schema(description = "돌봄 예약 날짜", pattern = "yyyy-MM-dd")
        private LocalDate reservationAt;

        @Schema(description = "돌봄 주소(우편번호)", pattern = "12345")
        private String zipcode;

        @Schema(description = "돌봄 주소(상세주소)")
        private String address;

        @Schema(description = "고객 전화번호", pattern = "010-xxxx-xxxx")
        private String phoneNumber; // 고객 전화번호

        @Schema(description = "고객 프로필 이미지")
        private String profileImage;    // 고객 프로필 이미지

        @Schema(description = "고객 요청사항")
        private String requests;

        @Schema(description = "돌봄 발생 시간", pattern = "yyyy-MM-dd:HH:mm:ss")
        private LocalDateTime createdAt;

        @Schema(description = "예약 상태", pattern = "RESERVATION(예약완료), CANCEL(취소)")
        private ReservationStatus status;

        @Schema(description = "돌봄에 맡겨진 반려견")
        private List<PetReservationResponse.PetDetailResponse> pets;

        @Schema(description = "작성된 돌봄 케어 로그")
        private List<CareLogResponse.GetDetail> careLogList;

        @Schema(description = "작성된 돌봄 리뷰")
        private ReviewResponse.GetDetail review;

        public GetDetail(Member customer, Member sitter, SitterSchedule sitterSchedule, List<PetReservation> pets, List<CareLog> careLogList, Integer usingPoint, int originalPrice) {
            this.id = sitterSchedule.getId();
            this.customerId = customer.getId();
            this.customerNickName = customer.getNickName();
            this.sitterName = sitter.getName();
            this.usingPoint = usingPoint;
            this.originalPrice = originalPrice;
            this.resultPrice = sitterSchedule.getPrice();
            this.reservationAt = sitterSchedule.getReservationAt();
            this.zipcode = sitter.getZipcode();
            this.address = sitter.getAddress();
            this.phoneNumber = customer.getPhoneNumber();
            this.profileImage = customer.getProfileImage();
            this.requests = sitterSchedule.getRequests();
            this.createdAt = sitterSchedule.getCreatedAt();
            this.status = sitterSchedule.getStatus();
            this.pets = pets
                    .stream()
                    .map(PetReservationResponse.PetDetailResponse::new)
                    .toList();
            this.careLogList = careLogList.stream()
                    .filter(c -> !c.isDeleted())
                    .sorted(Comparator.comparing(CareLog::getId).reversed()) // CareLog ID 기준 내림차순 정렬
                    .map(c -> {
                        return new CareLogResponse.GetDetail(c.getId(), c.getSitterSchedule().getSitter().getName(), c.getCareType(),
                                c.getDescription(), c.getImage(), c.getCreatedAt());
                    }).toList();
            this.review = Optional.ofNullable(sitterSchedule.getCustomerReservation().getReview())
                    .filter(r -> !r.isDeleted())
                    .map(r -> {
                        return new ReviewResponse.GetDetail(r.getId(), r.getCustomerReservation().getId(), r.getCustomerReservation().getCustomer().getNickName(),
                                r.getCustomerReservation().getSitter().getName(), r.getCustomerReservation().getReservationAt(), r.getRating(), r.getComment(), r.getCreatedAt());
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
