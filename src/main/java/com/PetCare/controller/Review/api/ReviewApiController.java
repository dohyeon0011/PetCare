package com.PetCare.controller.Review.api;

import com.PetCare.dto.Review.request.AddReviewRequest;
import com.PetCare.dto.Review.request.UpdateReviewRequest;
import com.PetCare.dto.Review.response.ReviewResponse;
import com.PetCare.service.Review.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/PetsCare")
public class ReviewApiController {

    private final ReviewService reviewService;

    @Operation(description = "리뷰 작성 API")
    @PostMapping("/members/{customerId}/reservations/{customerReservationId}/reviews/new")
    public ResponseEntity<ReviewResponse.GetDetail> saveReview(@PathVariable("customerId") long customerId,
                                                               @PathVariable("customerReservationId") long reservationId,
                                                               @RequestBody @Valid AddReviewRequest request) {
        ReviewResponse.GetDetail review = reviewService.save(customerId, reservationId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(review);
    }

    @Operation(description = "특정 회원의 모든 리뷰 조회 API")
    @GetMapping("/members/{customerId}/reviews")
    public ResponseEntity<List<ReviewResponse.GetList>> findAllReview(@PathVariable("customerId") long customerId) {
        List<ReviewResponse.GetList> reviews = reviewService.findAllById(customerId);

        return ResponseEntity.ok()
                .body(reviews);
    }

    @Operation(description = "특정 리뷰 조회")
    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewResponse.GetDetail> findReview(@PathVariable("reviewId") long reviewId) {
        ReviewResponse.GetDetail review = reviewService.findById(reviewId);

        return ResponseEntity.ok()
                .body(review);
    }

    @Operation(description = "특정 리뷰 삭제")
    @DeleteMapping("/members/{customerId}/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable("customerId") long customerId, @PathVariable("reviewId") long reviewId) {
        reviewService.delete(customerId, reviewId);

        return ResponseEntity.ok()
                .build();
    }

    @Operation(description = "특정 리뷰 수정")
    @PutMapping("/members/{customerId}/reviews/{reviewId}")
    public ResponseEntity<ReviewResponse.GetDetail> updateReview(@PathVariable("customerId") long customerId,
                                                                 @PathVariable("reviewId") long reviewId,
                                                                 @RequestBody @Valid UpdateReviewRequest request) {
        ReviewResponse.GetDetail updateReview = reviewService.update(customerId, reviewId, request);

        return ResponseEntity.ok()
                .body(updateReview);
    }

}