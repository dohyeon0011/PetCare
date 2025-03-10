package com.PetSitter.controller.Certification.view;

import com.PetSitter.domain.Member.MemberDetails;
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
    public String newCertification(@AuthenticationPrincipal MemberDetails memberDetails, Model model) {
        if (memberDetails.getMember() != null) {
            model.addAttribute("currentUser", memberDetails.getMember());
        }

        return "certification/new-certification";
    }

    @Comment("회원의 자격증 수정")
    @GetMapping("/members/{sitterId}/certifications/edit")
    public String editCertification(@PathVariable("sitterId") long sitterId, @AuthenticationPrincipal MemberDetails memberDetails, Model model) {
        if(memberDetails.getMember().getId() == sitterId) {
            List<CertificationResponse.GetList> certifications = certificationService.findById(sitterId);
            model.addAttribute("certifications", certifications);
            model.addAttribute("currentUser", memberDetails.getMember());
        }

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
