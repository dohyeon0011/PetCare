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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

// 관리자 페이지(돌봄 예약 조회)
public class AdminReservationResponse {

    @NoArgsConstructor
    @Getter
    public static class ReservationListResponse {
        private long id;
        private String customerNickName;
        private String sitterName;
        private LocalDate reservationAt;
        private LocalDateTime createdAt;
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
    public static class ReservationDetailResponse {
        private long id;
        private String customerNickName;
        private String sitterName;
        private Integer usingPoint; // 고객이 사용한 포인트
        private int originalPrice;  // 원래 돌봄 예약 가격
        private int resultPrice;    // 최종 가격(원래 돌봄 가격 - 고객 사용 포인트)
        private LocalDate reservationAt;
        private String zipcode;
        private String address;
        private String customerPhoneNumber;  // 고객 전화번호
        private String sitterPhoneNumber;    // 펫시터 전화번호
        private String profileImage; // 펫시터 프로필 이미지
        private String requests;
        private LocalDateTime createdAt;
        private ReservationStatus status;
        private List<PetReservationResponse.PetDetailResponse> pets;
        private List<CareLogResponse.GetDetail> careLogList;
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
