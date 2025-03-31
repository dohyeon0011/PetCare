package com.PetSitter.controller.Review.api;

import com.PetSitter.config.exception.BadRequestCustom;
import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.MemberDetails;
import com.PetSitter.domain.Member.oauth.CustomOAuth2User;
import com.PetSitter.domain.Review.ReviewSearch;
import com.PetSitter.dto.Review.request.AddReviewRequest;
import com.PetSitter.dto.Review.request.UpdateReviewRequest;
import com.PetSitter.dto.Review.response.ReviewResponse;
import com.PetSitter.service.Review.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets-care")
public class ReviewApiController {

    private final ReviewService reviewService;

    @Operation(summary = "고객 - 리뷰 작성", description = "리뷰 작성 API")
    @PostMapping("/members/{customerId}/reservations/{customerReservationId}/reviews/new")
    public ResponseEntity<ReviewResponse.GetDetail> saveReview(@PathVariable("customerId") @Parameter(required = true, description = "회원(고객) 고유 번호") long customerId,
                                                               @PathVariable("customerReservationId") @Parameter(required = true, description = "예약 고유 번호") long reservationId,
                                                               @RequestBody @Valid AddReviewRequest request, @AuthenticationPrincipal Object principal) {
        if (principal instanceof MemberDetails) {
            Member member = ((MemberDetails) principal).getMember();

            if (member.getId() != customerId) {
                throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
            }
        } else if (principal instanceof CustomOAuth2User) {
            Member member = ((CustomOAuth2User) principal).getMember();

            if (member.getId() != customerId) {
                throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
            }
        }

        ReviewResponse.GetDetail review = reviewService.save(customerId, reservationId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(review);
    }

    @Operation(summary = "모든 리뷰 조회", description = "모든 리뷰 조회 API")
    @GetMapping("/members/{memberId}/reviews")
    public ResponseEntity<Page<ReviewResponse.GetList>> findAllReview(@PathVariable("memberId") @Parameter(required = true, description = "회원 고유 번호") long memberId,
                                                                      @Parameter(description = "페이징 파라미터, page: 페이지 번호 - 0부터 시작, size: 한 페이지의 데이터 개수") Pageable pageable, @AuthenticationPrincipal Object principal) {
//        List<ReviewResponse.GetList> reviews = reviewService.findAllById(memberId);

        if (principal instanceof MemberDetails) {
            Member member = ((MemberDetails) principal).getMember();

            if (member.getId() != memberId) {
                throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
            }
        } else if (principal instanceof CustomOAuth2User) {
            Member member = ((CustomOAuth2User) principal).getMember();

            if (member.getId() != memberId) {
                throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
            }
        }

        Page<ReviewResponse.GetList> reviews = reviewService.findAllById(memberId, pageable);

        return ResponseEntity.ok()
                .body(reviews);
    }

    @Operation(summary = "특정 리뷰 조회", description = "특정 리뷰 조회 API")
    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewResponse.GetDetail> findReview(@PathVariable("reviewId") @Parameter(required = true, description = "리뷰 고유 번호") long reviewId) {
        ReviewResponse.GetDetail review = reviewService.findById(reviewId);

        return ResponseEntity.ok()
                .body(review);
    }

    @Operation(summary = "고객 - 특정 리뷰 삭제", description = "특정 리뷰 삭제 API")
    @DeleteMapping("/members/{customerId}/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable("customerId") @Parameter(required = true, description = "회원(고객) 고유 번호") long customerId,
                                             @PathVariable("reviewId") @Parameter(required = true, description = "리뷰 고유 번호") long reviewId, @AuthenticationPrincipal Object principal) {
        if (principal instanceof MemberDetails) {
            Member member = ((MemberDetails) principal).getMember();

            if (member.getId() != customerId) {
                throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
            }
        } else if (principal instanceof CustomOAuth2User) {
            Member member = ((CustomOAuth2User) principal).getMember();

            if (member.getId() != customerId) {
                throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
            }
        }

        reviewService.delete(customerId, reviewId);

        return ResponseEntity.noContent()
                .build();
    }

    @Operation(summary = "고객 - 특정 리뷰 수정", description = "특정 리뷰 수정 API")
    @PutMapping("/members/{customerId}/reviews/{reviewId}")
    public ResponseEntity<ReviewResponse.GetDetail> updateReview(@PathVariable("customerId") @Parameter(required = true, description = "회원(고객) 고유 번호") long customerId,
                                                                 @PathVariable("reviewId") @Parameter(required = true, description = "리뷰 고유 번호") long reviewId,
                                                                 @RequestBody @Valid UpdateReviewRequest request,
                                                                 @AuthenticationPrincipal Object principal) {
        if (principal instanceof MemberDetails) {
            Member member = ((MemberDetails) principal).getMember();

            if (member.getId() != customerId) {
                throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
            }
        } else if (principal instanceof CustomOAuth2User) {
            Member member = ((CustomOAuth2User) principal).getMember();

            if (member.getId() != customerId) {
                throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
            }
        }

        ReviewResponse.GetDetail updateReview = reviewService.update(customerId, reviewId, request);

        return ResponseEntity.ok()
                .body(updateReview);
    }

    @Operation(summary = "리뷰 작성 시 보여질 폼 데이터", description = "리뷰 작성 시 보여질 폼 데이터")
    @GetMapping("/members/{customerId}/reservations/{customerReservationId}/reviews/new")
    public ResponseEntity<ReviewResponse.GetNewReview> getReview(@PathVariable("customerId") @Parameter(required = true, description = "회원(고객) 고유 번호") long customerId,
                                                                 @PathVariable("customerReservationId") @Parameter(required = true, description = "예약 고유 번호") long customerReservationId,
                                                                 @AuthenticationPrincipal Object principal) {
        if (principal instanceof MemberDetails) {
            Member member = ((MemberDetails) principal).getMember();

            if (member.getId() != customerId) {
                throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
            }
        } else if (principal instanceof CustomOAuth2User) {
            Member member = ((CustomOAuth2User) principal).getMember();

            if (member.getId() != customerId) {
                throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
            }
        }

        ReviewResponse.GetNewReview newReview = reviewService.getNewReview(customerId, customerReservationId);

        return ResponseEntity.ok()
                .body(newReview);
    }

    /**
     * V2: 검색 조건을 검색 전용 클래스 객체로 받기
     * 1. 검색 조건이 추가되더라도 컨트롤러 메서드의 변경 없이 유지 가능
     * 2. 파라미터 개수가 많아져도 코드가 깔끔해짐
     * 3. 스프링이 자동으로 요청 파라미터를 객체 필드에 매핑해줌(@ModelAttribute를 사용하면 쿼리 파라미터를 자동으로 객체 필드에 매핑)
     */
    @Operation(summary = "이용 후기 페이지 - 전체 리뷰 및 특정 돌봄사의 리뷰 조회", description = "이용 후기 페이지에서 전체 리뷰 및 특정 돌봄사의 리뷰 조회 API: V2")
    @GetMapping("/reviews")
    public ResponseEntity<List<ReviewResponse.GetDetail>> getAllReviewsBySitterV2(@ModelAttribute @Parameter(description = "검색 파라미터: 회원(돌봄사) 이름") ReviewSearch reviewSearch,
                                                                                  @RequestParam(defaultValue = "0") @Parameter(description = "페이징 파라미터, page: 페이지 번호 - 0부터 시작, size: 한 페이지의 데이터 개수") int page) {
        List<ReviewResponse.GetDetail> reviews = reviewService.getAllReviewsBySitterV2(reviewSearch, page);

        return ResponseEntity.ok()
                .body(reviews);
    }

    /**
     * V2에 통합
     */
    /*@Operation(description = "이용 후기 페이지에서 전체 리뷰 조회 API")
    @GetMapping("/reviews-all")
    public ResponseEntity<List<ReviewResponse.GetDetail>> getAllReviews(@RequestParam(defaultValue = "0") int page) {
        List<ReviewResponse.GetDetail> reviews = reviewService.getAllReviews(page);

        return ResponseEntity.ok()
                .body(reviews);
    }*/

    /**
     * V1: 검색 조건 @RequestParam 으로 받기
     * 1. 검색 조건이 추가될 때마다 @RequestParam을 계속 추가해야 함
     * 2. 검색 조건이 많아지면 컨트롤러 메서드의 파라미터가 길어짐
     * 3. 유지보수가 어려워짐
     */
    /*@Operation(description = "이용 후기 페이지에서 특정 돌봄사의 리뷰 조회 API: V1")
    @GetMapping("/reviews-v1")
    public ResponseEntity<List<ReviewResponse.GetDetail>> getAllReviewsBySitterV1(@RequestParam("sitter") String sitterName,
                                                                                @RequestParam(defaultValue = "0") int page) {
        List<ReviewResponse.GetDetail> reviews = reviewService.getAllReviewsBySitterV1(sitterName, page);

        return ResponseEntity.ok()
                .body(reviews);
    }*/

}
