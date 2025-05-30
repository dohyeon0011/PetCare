package com.PetSitter.controller.Certification.view;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.MemberDetails;
import com.PetSitter.domain.Member.oauth.CustomOAuth2User;
import com.PetSitter.dto.Certification.response.CertificationResponse;
import com.PetSitter.service.Certification.CertificationService;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Comment;
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
public class CertificationViewController {

    private final CertificationService certificationService;

    @Comment("회원의 자격증 추가")
    @GetMapping("/certifications/new")
    public String newCertification(@AuthenticationPrincipal Object principal, Model model) {
        Member member;

        if (principal instanceof MemberDetails && ((MemberDetails) principal).getMember() != null) {   // 일반 폼 로그인 사용자의 경우
            member = ((MemberDetails) principal).getMember();
            model.addAttribute("currentUser", member);
        } else if (principal instanceof CustomOAuth2User && ((CustomOAuth2User) principal).getMember() != null) { // OAuth2 소셜 로그인 사용자의 경우
            member = ((CustomOAuth2User) principal).getMember();
            model.addAttribute("currentUser", member);
            model.addAttribute("isOAuthUser", true);
        }

        return "certification/new-certification";
    }

    @Comment("회원의 자격증 수정")
    @GetMapping("/members/{sitterId}/certifications/edit")
    public String editCertification(@PathVariable("sitterId") long sitterId, @AuthenticationPrincipal Object principal, Model model) {
        Member member;

        if (principal instanceof MemberDetails && ((MemberDetails) principal).getMember() != null) {   // 일반 폼 로그인 사용자의 경우
            member = ((MemberDetails) principal).getMember();
            model.addAttribute("currentUser", member);
        } else if (principal instanceof CustomOAuth2User && ((CustomOAuth2User) principal).getMember() != null) { // OAuth2 소셜 로그인 사용자의 경우
            member = ((CustomOAuth2User) principal).getMember();
            model.addAttribute("currentUser", member);
            model.addAttribute("isOAuthUser", true);
        }

        List<CertificationResponse.GetList> certifications = certificationService.findById(sitterId);
        model.addAttribute("certifications", certifications);

        return "certification/edit-certification";
    }

    /*@Comment("회원의 모든 자격증 조회")
    @GetMapping("/members/{sitterId}/certifications")
    public String getAllCertification(@PathVariable("sitterId") long sitterId, Model model) {
        List<CertificationResponse.GetList> certifications = certificationService.findById(sitterId);
        model.addAttribute("certifications", certifications);

        return "certification/certificationList";
    }*/
}
