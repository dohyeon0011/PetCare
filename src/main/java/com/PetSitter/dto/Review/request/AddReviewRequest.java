package com.PetSitter.dto.Review.request;

import com.PetSitter.domain.Reservation.CustomerReservation.CustomerReservation;
import com.PetSitter.domain.Review.Review;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Schema(description = "돌봄 리뷰 작성 Req DTO")
public class AddReviewRequest {

//    private long customerReservationId; url에서 읽을거임

    @Schema(description = "돌봄 평점", minLength = 0, maxLength = 5, defaultValue = "0")
    @NotNull(message = "평점을 0 ~ 5점 사이로 입력하세요.")
    @Min(value = 0, message = "평점은 최소 0점이어야 합니다.")
    @Max(value = 5, message = "평점은 최대 5점이어야 합니다.")
    private Double rating;

    @Schema(description = "돌봄 리뷰 내용")
    private String comment;

    public Review toEntity(CustomerReservation customerReservation) {
        return Review.builder()
                .customerReservation(customerReservation)
                .rating(rating)
                .comment(comment)
                .build();
    }

}
