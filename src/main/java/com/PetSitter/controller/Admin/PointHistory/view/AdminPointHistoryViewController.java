package com.PetSitter.controller.Admin.PointHistory.view;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.MemberDetails;
import com.PetSitter.domain.PointHistory.PointSearch;
import com.PetSitter.dto.PointHistory.response.AdminPointHistoryResponse;
import com.PetSitter.service.Admin.PointHistory.AdminPointHistoryService;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@RequiredArgsConstructor
@RequestMapping("/pets-care/admin")
public class AdminPointHistoryViewController {

    private final AdminPointHistoryService adminPointHistoryService;

    @Comment("관리자 페이지 - 회원 포인트 내역 전체 조회")
    @GetMapping("/amounts")
    public String findAllForAdmin(@ModelAttribute PointSearch pointSearch, @AuthenticationPrincipal MemberDetails memberDetails, @PageableDefault Pageable pageable, Model model) {
        if (memberDetails != null && memberDetails.getMember() != null) {
            Page<AdminPointHistoryResponse.PointListResponse> pointsHistory = adminPointHistoryService.findAllForAdmin(pointSearch, memberDetails.getMember(), pageable);
            model.addAttribute("pointsHistory", pointsHistory);
            model.addAttribute("currentUser", memberDetails.getMember());
            model.addAttribute("isLogin", memberDetails.getMember().getId());
        }

        return "admin/point-history/point-history-list";
    }
}
