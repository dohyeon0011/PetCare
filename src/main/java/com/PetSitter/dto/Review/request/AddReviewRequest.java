package com.PetSitter.dto.Review.request;

import com.PetSitter.domain.Reservation.CustomerReservation.CustomerReservation;
import com.PetSitter.domain.Review.Review;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddReviewRequest {

//    private long customerReservationId; url에서 읽을거임

    @NotNull(message = "평점을 0 ~ 5점 사이로 입력하세요.")
    @Min(value = 0, message = "평점은 최소 0점이어야 합니다.")
    @Max(value = 5, message = "평점은 최대 5점이어야 합니다.")
    private Double rating;

    private String comment;

    public Review toEntity(CustomerReservation customerReservation) {
        return Review.builder()
                .customerReservation(customerReservation)
                .rating(rating)
                .comment(comment)
                .build();
    }

}
