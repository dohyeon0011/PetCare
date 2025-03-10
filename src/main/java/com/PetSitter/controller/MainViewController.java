package com.PetSitter.controller;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.MemberDetails;
import com.PetSitter.dto.Review.response.ReviewResponse;
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

    private final ReviewService reviewService;

    @GetMapping("/main")
    public String home(@AuthenticationPrincipal MemberDetails memberDetails, Model model) {
        if (memberDetails != null) {
            model.addAttribute("currentUser", memberDetails.getMember());
        }

        List<ReviewResponse.GetDetail> reviews = reviewService.getAllReview();

        model.addAttribute("reviews", reviews);
        return "main";
    }

    @Comment("로그인")
    @GetMapping("/login")
    public String login(Model model) {
        return "member/login";
    }

}
