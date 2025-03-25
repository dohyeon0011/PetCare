package com.PetSitter.controller;

import com.PetSitter.domain.Member.MemberDetails;
import com.PetSitter.dto.Review.response.ReviewResponse;
import com.PetSitter.service.Member.MemberService;
import com.PetSitter.service.Review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public String home(@AuthenticationPrincipal MemberDetails memberDetails, Model model) {
        if (memberDetails != null) {
            model.addAttribute("currentUser", memberDetails.getMember());
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
    public String getInfo(@AuthenticationPrincipal MemberDetails memberDetails, Model model) {
        if (memberDetails != null) {
            model.addAttribute("currentUser", memberDetails.getMember());
        }

        return "intro";
    }

    @Comment("메인 페이지 - 자세히 보기 1 (펫시터의 집에서 돌봄을!) 안내")
    @GetMapping("/sitter")
    public String getSitter(@AuthenticationPrincipal MemberDetails memberDetails, Model model) {
        if (memberDetails != null) {
            model.addAttribute("currentUser", memberDetails.getMember());
        }

        List<ReviewResponse.GetDetail> reviews = reviewService.getAllReview();

        model.addAttribute("reviews", reviews);

        return "service-info";
    }

    @Comment("메인 페이지 - 자세히 보기 2 (고양이 돌봄 서비스) 안내")
    @GetMapping("/cat-care")
    public String getTrainer(@AuthenticationPrincipal MemberDetails memberDetails, Model model) {
        if (memberDetails != null) {
            model.addAttribute("currentUser", memberDetails.getMember());
        }

        return "cat-care-info";
    }

    @Comment("메인 페이지 - 자세히 보기 3 (어떤 분들이 활동하고 있나요?) 안내")
    @GetMapping("/sitters-information")
    public String getSittersInfo(@AuthenticationPrincipal MemberDetails memberDetails, Model model) {
        if (memberDetails != null) {
            model.addAttribute("currentUser", memberDetails.getMember());
        }

        Object bestSitters = memberService.findBestSitters();

        model.addAttribute("bestSitters", bestSitters);

        return "sitters-info";
    }

}
