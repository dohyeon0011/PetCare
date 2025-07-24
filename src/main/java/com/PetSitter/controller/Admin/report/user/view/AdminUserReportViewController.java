package com.PetSitter.controller.Admin.report.user.view;

import com.PetSitter.domain.Member.MemberDetails;
import com.PetSitter.dto.report.user.response.AdminUserReportResponse;
import com.PetSitter.service.Admin.report.user.AdminUserReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/pets-care/admin/reports")
public class AdminUserReportViewController {

    private final AdminUserReportService adminUserReportService;

    /**
     * 관리자 페이지 - 모든 유저 신고 내역 조회 페이지 반환
     */
    @GetMapping
    public String indexUserReport(@AuthenticationPrincipal MemberDetails memberDetails, @PageableDefault Pageable pageable, Model model) {
        if (memberDetails != null && memberDetails.getMember() != null) {
            Page<AdminUserReportResponse.UserReportListDTO> userReportList = adminUserReportService.indexUserReport(memberDetails.getMember(), pageable);
            model.addAttribute("userReportList", userReportList);
            model.addAttribute("currentUser", memberDetails.getMember());
            model.addAttribute("isLogin", memberDetails.getMember().getId());
        }
        return "admin/report/user/user-report-list";
    }

    /**
     * 관리자 페이지 - 유저 신고 문의 상세 조회 페이지 반환
     */
    @GetMapping("/{userReportId}/users")
    public String showUserReport(@PathVariable("userReportId") Long userReportId, @AuthenticationPrincipal MemberDetails memberDetails, Model model) {
        if (memberDetails != null && memberDetails.getMember() != null) {
            AdminUserReportResponse.UserReportDetailDTO userReport = adminUserReportService.showUserReport(memberDetails.getMember(), userReportId);
            model.addAttribute("userReport", userReport);
            model.addAttribute("currentUser", memberDetails.getMember());
            model.addAttribute("isLogin", memberDetails.getMember().getId());
        }
        return "admin/report/user/user-report-detail";
    }
}