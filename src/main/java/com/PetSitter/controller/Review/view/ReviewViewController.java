package com.PetSitter.controller.Review.view;

import com.PetSitter.dto.Review.response.ReviewResponse;
import com.PetSitter.service.Review.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/pets-care")
public class ReviewViewController {

    private final ReviewService reviewService;

    @Operation(description = "리뷰 작성")
    @GetMapping("/members/{customerId}/reservations/{customerReservationId}/reviews/new")
    public String newReview(@PathVariable("customerId") long customerId,
                            @PathVariable("customerReservationId") long customerReservationId, Model model) {
        ReviewResponse.GetNewReview response = reviewService.getNewReview(customerId, customerReservationId);
        model.addAttribute("response", response);

        return "review/new-review";
    }

    @Operation(description = "특정 회원의 모든 리뷰 조회")
    @GetMapping("/members/{customerId}/reviews")
    public String getAllReview(@PathVariable("customerId") long customerId, Pageable pageable, Model model) {
//        List<ReviewResponse.GetList> reviews = reviewService.findAllById(customerId);
        Page<ReviewResponse.GetList> reviews = reviewService.findAllById(customerId, pageable);
        model.addAttribute("reviews", reviews);

        return "review/review-list";
    }

    @Operation(description = "특정 리뷰 조회")
    @GetMapping("/reviews/{reviewId}")
    public String getReview(@PathVariable("reviewId") long reviewId, Model model) {
        ReviewResponse.GetDetail review = reviewService.findById(reviewId);
        model.addAttribute("review", review);

        return "review/review-detail";
    }

}
