package com.PetSitter.controller.Admin.Review.api;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.service.Review.AdminReviewService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets-care/admin")
public class AdminReviewApiController {

    private final AdminReviewService adminReviewService;

    @Operation(description = "관리자 권한 리뷰 삭제 API")
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> deleteForAdmin(@PathVariable("reviewId") long id, @SessionAttribute("member") Member member) {
        adminReviewService.deleteForAdmin(id, member);

        return ResponseEntity.noContent()
                .build();
    }

}
