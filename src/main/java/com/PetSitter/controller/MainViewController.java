package com.PetSitter.controller;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.MemberDetails;
import com.PetSitter.domain.Member.oauth.CustomOAuth2User;
import com.PetSitter.dto.Review.response.ReviewResponse;
import com.PetSitter.service.Member.MemberService;
import com.PetSitter.service.Review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/pets-care")
public class MainViewController {

    private final MemberService memberService;
    private final ReviewService reviewService;

    @Comment("메인 페이지")
    @GetMapping("/main")
    public String home(@AuthenticationPrincipal Object principal, Model model) {
        Member member;

        if (principal instanceof MemberDetails && ((MemberDetails) principal).getMember() != null) {   // 일반 폼 로그인 사용자의 경우
            member = ((MemberDetails) principal).getMember();
            model.addAttribute("currentUser", member);
        } else if (principal instanceof CustomOAuth2User && ((CustomOAuth2User) principal).getMember() != null) { // OAuth2 소셜 로그인 사용자의 경우
            member = ((CustomOAuth2User) principal).getMember();
            model.addAttribute("currentUser", member);
        }

        List<ReviewResponse.GetDetail> reviews = reviewService.getAllReview();

        model.addAttribute("reviews", reviews);
        return "main";
    }

    @Comment("로그인 페이지")
    @GetMapping("/login")
    public String login() {
        return "member/login";
    }

    @Comment("펫시터 정보 페이지(펫시터 서비스)")
    @GetMapping("/pet-sitter/information")
    public String getInfo(@AuthenticationPrincipal Object principal, Model model) {
        Member member;

        if (principal instanceof MemberDetails && ((MemberDetails) principal).getMember() != null) {   // 일반 폼 로그인 사용자의 경우
            member = ((MemberDetails) principal).getMember();
            model.addAttribute("currentUser", member);
        } else if (principal instanceof CustomOAuth2User && ((CustomOAuth2User) principal).getMember() != null) { // OAuth2 소셜 로그인 사용자의 경우
            member = ((CustomOAuth2User) principal).getMember();
            model.addAttribute("currentUser", member);
        }

        return "intro";
    }

    @Comment("메인 페이지 - 자세히 보기 1 (펫시터의 집에서 돌봄을!) 안내")
    @GetMapping("/sitter")
    public String getSitter(@AuthenticationPrincipal Object principal, Model model) {
        Member member;

        if (principal instanceof MemberDetails && ((MemberDetails) principal).getMember() != null) {   // 일반 폼 로그인 사용자의 경우
            member = ((MemberDetails) principal).getMember();
            model.addAttribute("currentUser", member);
        } else if (principal instanceof CustomOAuth2User && ((CustomOAuth2User) principal).getMember() != null) { // OAuth2 소셜 로그인 사용자의 경우
            member = ((CustomOAuth2User) principal).getMember();
            model.addAttribute("currentUser", member);
        }

        List<ReviewResponse.GetDetail> reviews = reviewService.getAllReview();

        model.addAttribute("reviews", reviews);

        return "service-info";
    }

    @Comment("메인 페이지 - 자세히 보기 2 (고양이 돌봄 서비스) 안내")
    @GetMapping("/cat-care")
    public String getTrainer(@AuthenticationPrincipal Object principal, Model model) {
        Member member;

        if (principal instanceof MemberDetails && ((MemberDetails) principal).getMember() != null) {   // 일반 폼 로그인 사용자의 경우
            member = ((MemberDetails) principal).getMember();
            model.addAttribute("currentUser", member);
        } else if (principal instanceof CustomOAuth2User && ((CustomOAuth2User) principal).getMember() != null) { // OAuth2 소셜 로그인 사용자의 경우
            member = ((CustomOAuth2User) principal).getMember();
            model.addAttribute("currentUser", member);
        }

        return "cat-care-info";
    }

    @Comment("메인 페이지 - 자세히 보기 3 (어떤 분들이 활동하고 있나요?) 안내")
    @GetMapping("/sitters-information")
    public String getSittersInfo(@AuthenticationPrincipal Object principal, Model model) {
        Member member;

        if (principal instanceof MemberDetails && ((MemberDetails) principal).getMember() != null) {   // 일반 폼 로그인 사용자의 경우
            member = ((MemberDetails) principal).getMember();
            model.addAttribute("currentUser", member);
        } else if (principal instanceof CustomOAuth2User && ((CustomOAuth2User) principal).getMember() != null) { // OAuth2 소셜 로그인 사용자의 경우
            member = ((CustomOAuth2User) principal).getMember();
            model.addAttribute("currentUser", member);
        }

        Object bestSitters = memberService.findBestSitters();

        model.addAttribute("bestSitters", bestSitters);

        return "sitters-info";
    }

}
