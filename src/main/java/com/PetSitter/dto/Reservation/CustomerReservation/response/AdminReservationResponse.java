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
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

// 관리자 페이지(돌봄 예약 조회)
@Schema(description = "관리자 페이지 - 예약 정보 조회 Response DTO")
public class AdminReservationResponse {

    @NoArgsConstructor
    @Getter
    @Schema(description = "관리자 페이지 - 돌봄 예약 정보 리스트 조회 Response DTO")
    public static class ReservationListResponse {
        @Schema(description = "예약 id")
        private long id;

        @Schema(description = "고객 닉네임(활동명)")
        private String customerNickName;

        @Schema(description = "돌봄사 이름(실명)")
        private String sitterName;

        @Schema(description = "돌봄 예약 날짜", pattern = "yyyy-MM-dd")
        private LocalDate reservationAt;

        @Schema(description = "돌봄 발생 시간", pattern = "yyyy-MM-dd:HH:mm:ss")
        private LocalDateTime createdAt;

        @Schema(description = "예약 상태", pattern = "RESERVATION(예약완료), CANCEL(취소)")
        private ReservationStatus status;

        public ReservationListResponse(long id, String customerNickName, String sitterName, LocalDate reservationAt, LocalDateTime createdAt, ReservationStatus status) {
            this.id = id;
            this.customerNickName = customerNickName;
            this.sitterName = sitterName;
            this.reservationAt = reservationAt;
            this.createdAt = createdAt;
            this.status = status;
        }
    }

    @NoArgsConstructor
    @Getter
    @Schema(description = "관리자 페이지 - 돌봄 예약 정보 상세 조회 Response DTO")
    public static class ReservationDetailResponse {
        @Schema(description = "예약 id")
        private long id;

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
        private String customerPhoneNumber;  // 고객 전화번호

        @Schema(description = "돌봄사 전화번호", pattern = "010-xxxx-xxxx")
        private String sitterPhoneNumber;    // 펫시터 전화번호

        @Schema(description = "돌봄사 프로필 이미지")
        private String profileImage; // 펫시터 프로필 이미지

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

        public ReservationDetailResponse(Member customer, Member sitter, CustomerReservation customerReservation, List<PetReservation> pets, List<CareLog> careLogList, Review review, Integer usingPoint, int originalPrice) {
            this.id = customerReservation.getId();
            this.customerNickName = customer.getNickName();
            this.sitterName = sitter.getName();
            this.usingPoint = usingPoint;
            this.originalPrice = originalPrice;
            this.resultPrice = customerReservation.getPrice();
            this.reservationAt = customerReservation.getReservationAt();
            this.zipcode = sitter.getZipcode();
            this.address = sitter.getAddress();
            this.customerPhoneNumber = customer.getPhoneNumber();
            this.sitterPhoneNumber = sitter.getPhoneNumber();
            this.profileImage = sitter.getProfileImage();
            this.requests = customerReservation.getRequests();
            this.createdAt = customerReservation.getCreatedAt();
            this.status = customerReservation.getStatus();
            this.pets = pets
                    .stream()
                    .map(PetReservationResponse.PetDetailResponse::new)
                    .toList();
            this.careLogList = careLogList
                    .stream()
                    .filter(careLog -> !careLog.isDeleted())
                    .sorted(Comparator.comparing(CareLog::getId).reversed())
                    .map(careLog -> new CareLogResponse.GetDetail(
                            careLog.getId(),
                            careLog.getSitterSchedule().getSitter().getName(),
                            careLog.getCareType(),
                            careLog.getDescription(),
                            careLog.getImage(),
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
