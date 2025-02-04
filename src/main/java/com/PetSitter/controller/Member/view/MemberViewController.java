package com.PetSitter.controller.Member.view;

import com.PetSitter.dto.Member.response.MemberResponse;
import com.PetSitter.dto.Review.response.ReviewResponse;
import com.PetSitter.service.Member.MemberService;
import com.PetSitter.service.Review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/pets-care")
public class MemberViewController {

    private final MemberService memberService;
    private final ReviewService reviewService;

    @Comment("회원가입")
    @GetMapping("/signup")
    public String newMember(Model model) {
        model.addAttribute("member", new MemberResponse());

        return "member/signup";
    }

    @Comment("로그인")
    @GetMapping("/login")
    public String login(Model model) {
        return "member/login";
    }

    @Comment("전체 회원 조회")
    @GetMapping("/members")
    public String getAllMember(Model model) {
        List<Object> members = memberService.findAll();
        model.addAttribute("members", members);

        return "admin/member/memberList";
    }

    @Comment("특정 회원 조회")
    @GetMapping("/members/{memberId}/myPage")
    public String getMember(@PathVariable("memberId") Long id, Model model) {
        Object member = memberService.findById(id);
        model.addAttribute("member", member);

        return "member/myPage";
    }

    @Comment("돌봄사 전체 조회")

    @Comment("회원 정보 수정")
    @GetMapping("/members/{memberId}/edit")
    public String editMember(@PathVariable("memberId") long id, Model model) {
        Object member = memberService.findById(id);
        model.addAttribute("member", member);

        return "member/editMember";
    }

    @GetMapping("/main")
    public String home(Model model) {

        List<ReviewResponse.GetDetail> reviews = reviewService.getAllReview();

        model.addAttribute("reviews", reviews);
        return "main";
    }
}

