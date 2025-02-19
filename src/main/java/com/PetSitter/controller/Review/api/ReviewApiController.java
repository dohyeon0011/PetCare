package com.PetSitter.controller.Review.api;

import com.PetSitter.dto.Review.request.AddReviewRequest;
import com.PetSitter.dto.Review.request.UpdateReviewRequest;
import com.PetSitter.dto.Review.response.ReviewResponse;
import com.PetSitter.service.Review.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets-care")
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
    @GetMapping("/members/{memberId}/reviews")
    public ResponseEntity<Page<ReviewResponse.GetList>> findAllReview(@PathVariable("memberId") long memberId, Pageable pageable) {
//        List<ReviewResponse.GetList> reviews = reviewService.findAllById(memberId);
        Page<ReviewResponse.GetList> reviews = reviewService.findAllById(memberId, pageable);

        return ResponseEntity.ok()
                .body(reviews);
    }

    @Operation(description = "특정 리뷰 조회 API")
    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewResponse.GetDetail> findReview(@PathVariable("reviewId") long reviewId) {
        ReviewResponse.GetDetail review = reviewService.findById(reviewId);

        return ResponseEntity.ok()
                .body(review);
    }

    @Operation(description = "특정 리뷰 삭제 API")
    @DeleteMapping("/members/{customerId}/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable("customerId") long customerId, @PathVariable("reviewId") long reviewId) {
        reviewService.delete(customerId, reviewId);

        return ResponseEntity.noContent()
                .build();
    }

    @Operation(description = "특정 리뷰 수정 API")
    @PutMapping("/members/{customerId}/reviews/{reviewId}")
    public ResponseEntity<ReviewResponse.GetDetail> updateReview(@PathVariable("customerId") long customerId,
                                                                 @PathVariable("reviewId") long reviewId,
                                                                 @RequestBody @Valid UpdateReviewRequest request) {
        ReviewResponse.GetDetail updateReview = reviewService.update(customerId, reviewId, request);

        return ResponseEntity.ok()
                .body(updateReview);
    }

    @Operation(description = "리뷰 작성 시 보여질 폼 데이터")
    @GetMapping("/members/{customerId}/reservations/{customerReservationId}/reviews/new")
    public ResponseEntity<ReviewResponse.GetNewReview> getReview(@PathVariable("customerId") long customerId,
                                                                 @PathVariable("customerReservationId") long customerReservationId) {
        ReviewResponse.GetNewReview newReview = reviewService.getNewReview(customerId, customerReservationId);

        return ResponseEntity.ok()
                .body(newReview);
    }

}
