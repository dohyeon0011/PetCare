package com.PetSitter.controller.Admin.Review.api;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.MemberDetails;
import com.PetSitter.service.Admin.Review.AdminReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets-care/admin")
public class AdminReviewApiController {

    private final AdminReviewService adminReviewService;

    @Operation(summary = "관리자 페이지 - 리뷰 삭제", description = "관리자 권한 리뷰 삭제 API")
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> deleteForAdmin(@PathVariable("reviewId") @Parameter(required = true, description = "리뷰 고유 번호") long id, @AuthenticationPrincipal MemberDetails memberDetails) {
        adminReviewService.deleteForAdmin(id, memberDetails.getMember());

        return ResponseEntity.noContent()
                .build();
    }

}
