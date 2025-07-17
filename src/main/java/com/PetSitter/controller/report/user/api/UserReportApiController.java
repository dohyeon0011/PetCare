package com.PetSitter.controller.report.user.api;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.MemberDetails;
import com.PetSitter.domain.Member.oauth.CustomOAuth2User;
import com.PetSitter.dto.report.user.request.AddUserReportRequest;
import com.PetSitter.dto.report.user.response.UserReportResponse;
import com.PetSitter.service.report.user.UserReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets-care/reports")
@Slf4j
public class UserReportApiController {

    private final UserReportService userReportService;

    @Operation(summary = "유저 신고 문의 등록", description = "유저 신고 문의 등록 API")
    @PostMapping("/users")
    public ResponseEntity<?> insertUserReport(@RequestBody @Valid AddUserReportRequest request, BindingResult result,
                                              @AuthenticationPrincipal Principal principal) {
        log.info("UserReportApiController - insertUserReport(): Call Success.");
        if (result.hasErrors()) {
            Map<String, String> errorRes = new HashMap<>();
            result.getFieldErrors().forEach(error -> {
                errorRes.put(error.getField(), error.getDefaultMessage());
            });

            return ResponseEntity.badRequest()
                    .body(errorRes);
        }

        Member reporter;
        if (principal instanceof MemberDetails) {
            reporter = ((MemberDetails) principal).getMember();
        } else if (principal instanceof CustomOAuth2User) {
            reporter = ((CustomOAuth2User) principal).getMember();
        } else {
            log.warn("Authentication Principal 타입 불일치: {}", principal.getClass().getName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("인증되지 않은 사용자입니다.");
        }
        UserReportResponse.UserReportDetailDTO newUserReport = userReportService.insertUserReport(reporter, request);

        return ResponseEntity.ok()
                .body(newUserReport);
    }

    @Operation(summary = "유저 신고 문의 내역 조회", description = "유저 신고 문의 내역 조회 API")
    @GetMapping("/users")
    public ResponseEntity<?> indexUserReport(@AuthenticationPrincipal Principal principal) {
        log.info("UserReportApiController - indexUserReport(): Call Success.");

        Member reporter;
        if (principal instanceof MemberDetails) {
            reporter = ((MemberDetails) principal).getMember();
        } else if (principal instanceof CustomOAuth2User) {
            reporter = ((CustomOAuth2User) principal).getMember();
        } else {
            log.warn("Authentication Principal 타입 불일치: {}", principal.getClass().getName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("인증되지 않은 사용자입니다.");
        }
        List<UserReportResponse.UserReportListDTO> findAllUserReport = userReportService.indexUserReport(reporter);

        return ResponseEntity.ok()
                .body(findAllUserReport);
    }

    @Operation(summary = "유저 신고 문의 내역 상세 조회", description = "유저 신고 문의 내역 상세 조회 API")
    @GetMapping("/{userReportId}/users")
    public ResponseEntity<?> showUserReport(@PathVariable("userReportId") @Parameter(name = "유저 신고 id") Long userReportId, @AuthenticationPrincipal Principal principal) {
        log.info("UserReportApiController - showUserReport(): Call Success.");

        Member reporter;
        if (principal instanceof MemberDetails) {
            reporter = ((MemberDetails) principal).getMember();
        } else if (principal instanceof CustomOAuth2User) {
            reporter = ((CustomOAuth2User) principal).getMember();
        } else {
            log.warn("Authentication Principal 타입 불일치: {}", principal.getClass().getName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("인증되지 않은 사용자입니다.");
        }
        UserReportResponse.UserReportDetailDTO findUserReport = userReportService.showUserReport(userReportId, reporter);

        return ResponseEntity.ok()
                .body(findUserReport);
    }

    @Operation(summary = "유저 신고 문의 내역 삭제", description = "유저 신고 문의 내역 삭제 API")
    @DeleteMapping("/{userReportId}/users")
    public ResponseEntity<?> deleteUserReport(@PathVariable("userReportId") @Parameter(name = "유저 신고 id") Long userReportId, @AuthenticationPrincipal Principal principal) {
        log.info("UserReportApiController - deleteUserReport(): Call Success.");

        Member reporter;
        if (principal instanceof MemberDetails) {
            reporter = ((MemberDetails) principal).getMember();
        } else if (principal instanceof CustomOAuth2User) {
            reporter = ((CustomOAuth2User) principal).getMember();
        } else {
            log.warn("Authentication Principal 타입 불일치: {}", principal.getClass().getName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("인증되지 않은 사용자입니다.");
        }
        userReportService.deleteUserReport(userReportId, reporter);

        return ResponseEntity.noContent()
                .build();
    }
}
