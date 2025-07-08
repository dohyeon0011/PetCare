package com.PetSitter.dto.Reservation;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Review.Review;
import com.PetSitter.dto.Certification.response.CertificationResponse;
import com.PetSitter.dto.Review.response.ReviewResponse;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "예약 전 예약 가능 페이지 - 돌봄사 정보 조회 Response DTO")
public class ReservationSitterResponse { // 고객이 예약하기 전 보여줄 정보

    @NoArgsConstructor
    @Getter
    @Schema(description = "예약 전 예약 가능 페이지 - 돌봄사 리스트")
    public static class GetList { // 예약 가능 페이지에 있는 돌봄사들의 정보
        @Schema(description = "돌봄사 id")
        private long sitterId;

        @Schema(description = "돌봄사 이름(실명)")
        private String sitterName;

        @Schema(description = "돌봄사 자기소개")
        private String introduction;

        @Schema(description = "돌봄사 경력연차")
        private Integer careerYear;

        @Schema(description = "돌봄사 프로필 이미지")
        private String profileImage;

        @Schema(description = "평균 리뷰 점수")
        private Double avgRating;

        @Schema(description = "총 리뷰 개수")
        private Long totalReviewCnt;

        @QueryProjection
        public GetList(long sitterId, String sitterName, String introduction, Integer careerYear, String profileImage, Double avgRating, Long totalReviewCnt) {
            this.sitterId = sitterId;
            this.sitterName = sitterName;
            this.introduction = introduction;
            this.careerYear = careerYear;
            this.profileImage = profileImage;
            this.avgRating = avgRating;
            this.totalReviewCnt = totalReviewCnt != null ? totalReviewCnt : 0;
        }
    }

    @NoArgsConstructor
    @Getter
    @Schema(description = "예약 전 예약 가능 페이지 - 돌봄사 상세 정보 조회 Response DTO")
    public static class GetDetail { // 예약 가능 페이지 목록 중 특정 돌봄사의 자세한 정보 + 해당 돌봄사에 대한 리뷰도 보여줄 것
        @Schema(description = "돌봄사 id")
        private long sitterId;

        @Schema(description = "돌봄사 이름(실명)")
        private String sitterName;

        @Schema(description = "돌봄사 자기소개")
        private String introduction;

        @Schema(description = "돌봄사 경력연차")
        private Integer careerYear;

        @Schema(description = "돌봄사 보유 자격증")
        private List<CertificationResponse.GetReservation> certifications;

        @Schema(description = "돌봄 주소(우편번호)", pattern = "11111")
        private String zipcode;

        @Schema(description = "돌봄 주소(상세주소)")
        private String address;

        @Schema(description = "돌봄사 프로필 이미지")
        private String profileImage;

        @Schema(description = "작성된 돌봄 리뷰")
        private List<ReviewResponse.GetDetail> reviews;

        @Schema(description = "평균 리뷰 점수")
        private Double avgRating;

        @Schema(description = "총 리뷰 개수")
        private Long totalReviewCnt;

        public GetDetail(Member sitter, List<Review> reviews, Double avgRating, Long totalReviewCnt) {
            this.sitterId = sitter.getId();
            this.sitterName = sitter.getName();
            this.introduction = (sitter.getIntroduction() != null) ?
                    sitter.getIntroduction().replace("\n", "<br>")
                    : "";
            this.careerYear = sitter.getCareerYear();
            this.certifications = sitter.getCertifications()
                    .stream()
                    .map(CertificationResponse.GetReservation::new)
                    .toList();
            this.zipcode = sitter.getZipcode();
            this.address = sitter.getAddress();
            this.profileImage = sitter.getProfileImage();
            this.reviews = reviews
                    .stream()
                    .map(review -> {
                        return new ReviewResponse.GetDetail(review.getId(), review.getCustomerReservation().getId(), review.getCustomerReservation().getCustomer().getNickName(),
                                review.getCustomerReservation().getSitter().getName(), review.getCustomerReservation().getReservationAt(), review.getRating(), review.getComment(), review.getCreatedAt());
                    })
                    .toList();
            this.avgRating = avgRating;
            this.totalReviewCnt = totalReviewCnt != null ? totalReviewCnt : 0;
        }
    }

    @NoArgsConstructor
    @Getter
    @Schema(description = "예약 전 예약 가능 페이지 - 돌봄사 평균 리뷰 점수 및 총 리뷰 개수 조회 Res DTO")
    public static class avgRatingAndTotalReviewsDTO {
        @Schema(description = "평균 리뷰 점수")
        private Double avgRating;

        @Schema(description = "총 리뷰 개수")
        private Long totalReviewCnt;

        public avgRatingAndTotalReviewsDTO(Double avgRating, Long totalReviewCnt) {
            this.avgRating = avgRating;
            this.totalReviewCnt = totalReviewCnt != null ? totalReviewCnt : 0;
        }
    }
}
