package com.PetSitter.controller.Admin.Member.view;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.dto.Member.response.AdminMemberResponse;
import com.PetSitter.service.Member.MemberService;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/pets-care/admin")
public class AdminMemberViewController {

    private final MemberService memberService;

    @Comment("관리자 페이지 모든 회원 목록 조회")
    @GetMapping("/members")
    public String getAllMember(@SessionAttribute("member") Member member, @PageableDefault Pageable pageable, Model model) {
        Page<AdminMemberResponse.MemberListResponse> members = memberService.findAllForAdmin(member, pageable);
        model.addAttribute("members", members);

        return "admin/member/member-list";
    }

    @Comment("관리자 페이지 회원 상세 정보 조회")
    @GetMapping("/members/{memberId}")
    public String getMemberDetail(@PathVariable("memberId") long id, @SessionAttribute("member") Member member, Model model) {
        Object findMember = memberService.findByIdForAdmin(id, member);
        model.addAttribute("member", findMember);

        return "admin/member/member-detail";
    }

}
