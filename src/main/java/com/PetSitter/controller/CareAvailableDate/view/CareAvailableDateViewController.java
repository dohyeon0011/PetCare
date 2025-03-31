package com.PetSitter.controller.CareAvailableDate.view;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.MemberDetails;
import com.PetSitter.domain.Member.oauth.CustomOAuth2User;
import com.PetSitter.dto.CareAvailableDate.response.CareAvailableDateResponse;
import com.PetSitter.service.CareAvailableDate.CareAvailableDateService;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/pets-care")
public class CareAvailableDateViewController {

    private final CareAvailableDateService careAvailableDateService;

    @Comment("돌봄 가능 일정 등록")
    @GetMapping("/care-available-dates/new")
    public String newCareAvailableDate(@AuthenticationPrincipal Object principal, Model model) {
        Member member;

        if (principal instanceof MemberDetails && ((MemberDetails) principal).getMember() != null) {   // 일반 폼 로그인 사용자의 경우
            member = ((MemberDetails) principal).getMember();
            model.addAttribute("currentUser", member);
        } else if (principal instanceof CustomOAuth2User && ((CustomOAuth2User) principal).getMember() != null) { // OAuth2 소셜 로그인 사용자의 경우
            member = ((CustomOAuth2User) principal).getMember();
            model.addAttribute("currentUser", member);
        }

        model.addAttribute("careAvailableDate", new CareAvailableDateResponse.GetList());

        return "careavailabledate/new-care-available-date";
    }

    @Comment("회원의 등록한 모든 돌봄 일정 조회")
    @GetMapping("/members/{sitterId}/care-available-dates")
    public String getCareAvailableDateList(@PathVariable("sitterId") long sitterId, @AuthenticationPrincipal Object principal, Model model, Pageable pageable) {
        Member member;

        if (principal instanceof MemberDetails && ((MemberDetails) principal).getMember() != null) {   // 일반 폼 로그인 사용자의 경우
            member = ((MemberDetails) principal).getMember();
            model.addAttribute("currentUser", member);
        } else if (principal instanceof CustomOAuth2User && ((CustomOAuth2User) principal).getMember() != null) { // OAuth2 소셜 로그인 사용자의 경우
            member = ((CustomOAuth2User) principal).getMember();
            model.addAttribute("currentUser", member);
        }

        Page<CareAvailableDateResponse.GetList> careAvailableDates = careAvailableDateService.findAllById(sitterId, pageable);
        model.addAttribute("careAvailableDates", careAvailableDates);

        return "careavailabledate/care-available-date-list";
    }

    @Comment("회원의 등록한 돌봄 일정 상세 조회")
    @GetMapping("/members/{sitterId}/care-available-dates/{careAvailableDateId}")
    public String getCareAvailableDate(@PathVariable("sitterId") long sitterId,
                                       @PathVariable("careAvailableDateId") long careAvailableDateId,
                                       @AuthenticationPrincipal Object principal, Model model) {
        Member member;

        if (principal instanceof MemberDetails && ((MemberDetails) principal).getMember() != null) {   // 일반 폼 로그인 사용자의 경우
            member = ((MemberDetails) principal).getMember();
            model.addAttribute("currentUser", member);
        } else if (principal instanceof CustomOAuth2User && ((CustomOAuth2User) principal).getMember() != null) { // OAuth2 소셜 로그인 사용자의 경우
            member = ((CustomOAuth2User) principal).getMember();
            model.addAttribute("currentUser", member);
        }

        CareAvailableDateResponse.GetDetail careAvailableDate = careAvailableDateService.findById(sitterId, careAvailableDateId);
        model.addAttribute("careAvailableDate", careAvailableDate);

        return "careavailabledate/care-available-date-detail";
    }

    @Comment("회원의 등록한 돌봄 가능 일정 수정")
    @GetMapping("/members/{sitterId}/care-available-dates/{careAvailableDateId}/edit")
    public String editCareAvailableDate(@PathVariable("sitterId") long sitterId, @PathVariable("careAvailableDateId") long careAvailableDateId,
                                        @AuthenticationPrincipal Object principal, Model model) {
        Member member;

        if (principal instanceof MemberDetails && ((MemberDetails) principal).getMember() != null) {   // 일반 폼 로그인 사용자의 경우
            member = ((MemberDetails) principal).getMember();
            model.addAttribute("currentUser", member);
        } else if (principal instanceof CustomOAuth2User && ((CustomOAuth2User) principal).getMember() != null) { // OAuth2 소셜 로그인 사용자의 경우
            member = ((CustomOAuth2User) principal).getMember();
            model.addAttribute("currentUser", member);
        }

        CareAvailableDateResponse.GetDetail careAvailableDate = careAvailableDateService.findById(sitterId, careAvailableDateId);
        model.addAttribute("careAvailableDate", careAvailableDate);

        return "careavailabledate/edit-care-available-date";
    }
}
