package com.PetSitter.controller.Admin.PointHistory.api;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.PointHistory.PointSearch;
import com.PetSitter.dto.PointHistory.response.AdminPointHistoryResponse;
import com.PetSitter.service.Admin.PointHistory.AdminPointHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets-care/admin")
public class AdminPointHistoryApiController {

    private final AdminPointHistoryService adminPointHistoryService;

    @Operation(description = "관리자 페이지 - 회원 포인트 내역 전체 조회 API")
    @GetMapping("/amounts")
    public ResponseEntity<Page<AdminPointHistoryResponse.PointListResponse>> findAllForAdmin(@ModelAttribute PointSearch pointSearch, @SessionAttribute("member") Member member, @PageableDefault Pageable pageable) {
        Page<AdminPointHistoryResponse.PointListResponse> pointsHistory = adminPointHistoryService.findAllForAdmin(pointSearch, member, pageable);

        return ResponseEntity.ok()
                .body(pointsHistory);
    }
}
