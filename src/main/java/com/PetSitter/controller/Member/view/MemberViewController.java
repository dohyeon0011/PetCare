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

    @Comment("회원가입")
    @GetMapping("/signup")
    public String newMember(Model model) {
        model.addAttribute("member", new MemberResponse());

        return "member/signup";
    }

    @Comment("특정 회원 조회")
    @GetMapping("/members/{memberId}/myPage")
    public String getMember(@PathVariable("memberId") Long id, Model model) {
        Object member = memberService.findById(id);
        model.addAttribute("member", member);

        return "member/mypage";
    }

    @Comment("회원 정보 수정")
    @GetMapping("/members/{memberId}/edit")
    public String editMember(@PathVariable("memberId") long id, Model model) {
        Object member = memberService.findById(id);
        model.addAttribute("member", member);

        return "member/edit-member";
    }

}

