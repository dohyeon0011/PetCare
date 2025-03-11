package com.PetSitter.controller.Admin.PointHistory.api;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.MemberDetails;
import com.PetSitter.domain.PointHistory.PointSearch;
import com.PetSitter.dto.PointHistory.response.AdminPointHistoryResponse;
import com.PetSitter.service.Admin.PointHistory.AdminPointHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets-care/admin")
public class AdminPointHistoryApiController {

    private final AdminPointHistoryService adminPointHistoryService;

    @Operation(summary = "관리자 페이지 - 회원 포인트 내역 전체 조회", description = "관리자 권한 - 회원 포인트 내역 전체 조회 API")
    @GetMapping("/amounts")
    public ResponseEntity<Page<AdminPointHistoryResponse.PointListResponse>> findAllForAdmin(@ModelAttribute @Parameter(description = "검색 파라미터: 회원(고객) 이름 ex)\"\" 전체 조회") PointSearch pointSearch, @AuthenticationPrincipal MemberDetails memberDetails,
                                                                                             @PageableDefault @Parameter(description = "페이징 파라미터, page: 페이지 번호 - 0부터 시작, size: 한 페이지의 데이터 개수") Pageable pageable) {
        Page<AdminPointHistoryResponse.PointListResponse> pointsHistory = adminPointHistoryService.findAllForAdmin(pointSearch, memberDetails.getMember(), pageable);

        return ResponseEntity.ok()
                .body(pointsHistory);
    }
}
