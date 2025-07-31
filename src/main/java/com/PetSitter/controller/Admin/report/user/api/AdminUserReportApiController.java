package com.PetSitter.controller.Admin.report.user.api;

import com.PetSitter.domain.Member.MemberDetails;
import com.PetSitter.dto.report.user.request.AdminUpdateUserReportRequest;
import com.PetSitter.dto.report.user.response.AdminUserReportResponse;
import com.PetSitter.service.Admin.report.user.AdminUserReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets-care/admin/reports")
@Slf4j
public class AdminUserReportApiController {

    private final AdminUserReportService adminUserReportService;

    @Operation(summary = "관리자 페이지 - 모든 유저 신고 문의 내역 조회", description = "관리자 권한 - 모든 유저 신고 내역 조회 API")
    @GetMapping("/users")
    public ResponseEntity<Page<AdminUserReportResponse.UserReportListDTO>> indexUserReport(@AuthenticationPrincipal MemberDetails memberDetails, @PageableDefault @Parameter(description = "페이징 파라미터, page: 페이지 번호 - 0부터 시작, size: 한 페이지의 데이터 개수") Pageable pageable) {
        Page<AdminUserReportResponse.UserReportListDTO> userReportListDTOS = adminUserReportService.indexUserReport(memberDetails.getMember(), pageable);

        return ResponseEntity.ok()
                .body(userReportListDTOS);
    }

    @Operation(summary = "관리자 페이지 - 유저 신고 문의 상세 조회", description = "관리자 권한 - 유저 신고 문의 상세 조회 API")
    @GetMapping("/{userReportId}/users")
    public ResponseEntity<AdminUserReportResponse.UserReportDetailDTO> showUserReport(@PathVariable("userReportId") Long userReportId, @AuthenticationPrincipal MemberDetails memberDetails) {
        AdminUserReportResponse.UserReportDetailDTO userReportDetailDTO = adminUserReportService.showUserReport(memberDetails.getMember(), userReportId);

        return ResponseEntity.ok()
                .body(userReportDetailDTO);
    }

    @Operation(summary = "관리자 페이지 - 유저 신고 문의 답변", description = "관리자 권한 - 유저 신고 문의 답변 API")
    @PutMapping("/{userReportId}/users")
    public ResponseEntity<?> updateUserReport(@PathVariable("userReportId") Long userReportId, @AuthenticationPrincipal MemberDetails memberDetails, @RequestBody @Valid AdminUpdateUserReportRequest request, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errorRes = new HashMap<>();
            result.getFieldErrors().forEach(error -> {
                errorRes.put(error.getField(), error.getDefaultMessage());
            });
            return ResponseEntity.badRequest()
                    .body(errorRes);
        }
        adminUserReportService.updateUserReport(memberDetails.getMember(), userReportId, request);

        return ResponseEntity.noContent()
                .build();
    }
}
