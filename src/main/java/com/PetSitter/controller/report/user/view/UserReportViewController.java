package com.PetSitter.controller.report.user.view;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.MemberDetails;
import com.PetSitter.domain.Member.oauth.CustomOAuth2User;
import com.PetSitter.dto.report.user.response.UserReportResponse;
import com.PetSitter.service.Member.MemberService;
import com.PetSitter.service.report.user.UserReportService;
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
@RequestMapping("/pets-care/reports")
public class UserReportViewController {

    private final UserReportService userReportService;
    private final MemberService memberService;

    /**
     * 새로운 유저 신고 문의 등록 페이지 반환
     */
    @GetMapping("/users/{memberId}/new")
    public String newUserReport(@PathVariable("memberId") Long memberId, @AuthenticationPrincipal Object principal, Model model) {
        Member member;
        if (principal instanceof MemberDetails && ((MemberDetails) principal).getMember() != null) {
            member = ((MemberDetails) principal).getMember();
            model.addAttribute("currentUser", member);
        } else if (principal instanceof CustomOAuth2User && ((CustomOAuth2User) principal).getMember() != null) {
            member = ((CustomOAuth2User) principal).getMember();
            model.addAttribute("currentUser", member);
            model.addAttribute("isOAuthUser", true);
        }
        Object reportedUser = memberService.findById(memberId);
        model.addAttribute("reportedUser", reportedUser);

        return "report/user/new-user-report";
    }

    /**
     * 나의 유저 신고 문의 내역 조회 페이지 반환
     */
    @GetMapping("/users")
    public String indexUserReport(@AuthenticationPrincipal Object principal, @PageableDefault Pageable pageable, Model model) {
        Member member = null;
        if (principal instanceof MemberDetails && ((MemberDetails) principal).getMember() != null) {
            member = ((MemberDetails) principal).getMember();
            model.addAttribute("currentUser", member);
        } else if (principal instanceof CustomOAuth2User && ((CustomOAuth2User) principal).getMember() != null) {
            member = ((CustomOAuth2User) principal).getMember();
            model.addAttribute("currentUser", member);
            model.addAttribute("isOAuthUser", true);
        }
        Page<UserReportResponse.UserReportListDTO> findUserReportList = userReportService.indexUserReport(member, pageable);
        model.addAttribute("myUserReports", findUserReportList);

        return "report/user/user-report-list";
    }

    /**
     * 나의 유저 신고 문의 내역 상세 조회 페이지 반환
     */
    @GetMapping("/{userReportId}/users")
    public String showUserReport(@PathVariable("userReportId") Long userReportId, @AuthenticationPrincipal Object principal, Model model) {
        Member member = null;
        if (principal instanceof MemberDetails && ((MemberDetails) principal).getMember() != null) {
            member = ((MemberDetails) principal).getMember();
            model.addAttribute("currentUser", member);
        } else if (principal instanceof CustomOAuth2User && ((CustomOAuth2User) principal).getMember() != null) {
            member = ((CustomOAuth2User) principal).getMember();
            model.addAttribute("currentUser", member);
            model.addAttribute("isOAuthUser", true);
        }
        UserReportResponse.UserReportDetailDTO findUserReport = userReportService.showUserReport(userReportId, member);
        model.addAttribute("userReport", findUserReport);

        return "report/user/user-report-detail";
    }
}
