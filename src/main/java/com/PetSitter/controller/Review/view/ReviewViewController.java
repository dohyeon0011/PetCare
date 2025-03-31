package com.PetSitter.controller.Review.view;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.MemberDetails;
import com.PetSitter.domain.Member.oauth.CustomOAuth2User;
import com.PetSitter.domain.Review.ReviewSearch;
import com.PetSitter.dto.CareLog.response.CareLogResponse;
import com.PetSitter.dto.Review.response.ReviewResponse;
import com.PetSitter.service.Review.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/pets-care")
public class ReviewViewController {

    private final ReviewService reviewService;

    /*@Operation(description = "리뷰 작성")
    @GetMapping("/members/{customerId}/reservations/{customerReservationId}/reviews/new")
    public String newReview(@PathVariable("customerId") long customerId,
                            @PathVariable("customerReservationId") long customerReservationId,
                            @AuthenticationPrincipal MemberDetails memberDetails, Model model) {
        if (memberDetails.getMember().getId() == customerId) {
            ReviewResponse.GetNewReview response = reviewService.getNewReview(customerId, customerReservationId);
            model.addAttribute("response", response);
            model.addAttribute("currentUser", memberDetails.getMember());
        }

        return "review/new-review";
    }*/

    @Operation(description = "특정 회원의 모든 리뷰 조회")
    @GetMapping("/members/{customerId}/reviews")
    public String getAllReview(@PathVariable("customerId") long customerId, Pageable pageable, @AuthenticationPrincipal Object principal, Model model) {
//        List<ReviewResponse.GetList> reviews = reviewService.findAllById(customerId);

        Member member;

        if (principal instanceof MemberDetails && ((MemberDetails) principal).getMember() != null) {   // 일반 폼 로그인 사용자의 경우
            member = ((MemberDetails) principal).getMember();
            model.addAttribute("currentUser", member);
        } else if (principal instanceof CustomOAuth2User && ((CustomOAuth2User) principal).getMember() != null) { // OAuth2 소셜 로그인 사용자의 경우
            member = ((CustomOAuth2User) principal).getMember();
            model.addAttribute("currentUser", member);
        }

        Page<ReviewResponse.GetList> reviews = reviewService.findAllById(customerId, pageable);
        model.addAttribute("reviews", reviews);

        return "review/review-my-list";
    }

    /*@Operation(description = "특정 리뷰 조회")
    @GetMapping("/reviews/{reviewId}")
    public String getReview(@PathVariable("reviewId") long reviewId, @AuthenticationPrincipal MemberDetails memberDetails, Model model) {
        if (memberDetails.getMember().getId() == customerId) {
            ReviewResponse.GetDetail review = reviewService.findById(reviewId);
            model.addAttribute("review", review);
            model.addAttribute("currentUser", memberDetails.getMember());
        }

        return "review/review-detail";
    }*/

    /*@Operation(description = "리뷰 수정")
    @GetMapping("/reviews/{reviewId}/edit")
    public String editReview(@PathVariable("reviewId") long reviewId, @AuthenticationPrincipal MemberDetails memberDetails, Model model) {
        if (memberDetails.getMember().getId() == customerId) {
            ReviewResponse.GetDetail review = reviewService.findById(reviewId);
            model.addAttribute("review", review);
            model.addAttribute("currentUser", memberDetails.getMember());
        }

        return "review/edit-review";
    }*/

    /**
     * API V2로 통합
     */
    /*@Operation(description = "이용 후기 페이지에서 전체 리뷰 조회")
    @GetMapping("/reviews-all")
    public String getAllReviews(@RequestParam(defaultValue = "0") int page, Model model) {
        List<ReviewResponse.GetDetail> reviews = reviewService.getAllReviews(page);
        model.addAttribute("reviews", reviews);

        return "review/review-all-list";
    }*/

    @Operation(description = "이용 후기 페이지에서 전체 리뷰 및 특정 돌봄사의 리뷰 조회")
    @GetMapping("/reviews")
    public String getAllReviewsBySitter(@ModelAttribute ReviewSearch reviewSearch, @RequestParam(defaultValue = "0") int page, @AuthenticationPrincipal Object principal, Model model) {
        Member member;

        if (principal instanceof MemberDetails && ((MemberDetails) principal).getMember() != null) {   // 일반 폼 로그인 사용자의 경우
            member = ((MemberDetails) principal).getMember();
            model.addAttribute("currentUser", member);
        } else if (principal instanceof CustomOAuth2User && ((CustomOAuth2User) principal).getMember() != null) { // OAuth2 소셜 로그인 사용자의 경우
            member = ((CustomOAuth2User) principal).getMember();
            model.addAttribute("currentUser", member);
        }

        List<ReviewResponse.GetDetail> reviews = reviewService.getAllReviewsBySitterV2(reviewSearch, page);
        List<String> sitters = reviewService.getAllSitters();

        // 전체 리뷰 개수 조회
        long totalReviews = reviewService.countAllReviewsBySitter(reviewSearch);
        int totalPages = (int) Math.ceil((double) totalReviews / 15); // 한 페이지 당 항목 수 15개

        model.addAttribute("reviews", reviews); // 리뷰 목록
        model.addAttribute("reviewSearch", reviewSearch); // 검색 조건
        model.addAttribute("totalReviews", totalReviews); // 총 리뷰 개수 (이런 것들 모두 Pageable을 사용하지 않아서 일일이 모델에 담아줘야함.)
        model.addAttribute("totalPages", totalPages); // 총 페이지 수
        model.addAttribute("currentPage", page); // 현재 페이지
        model.addAttribute("sitters", sitters); // 검색 드롭다운에 전체 돌봄사 목록 전달

        return "review/review-list";
    }

}
