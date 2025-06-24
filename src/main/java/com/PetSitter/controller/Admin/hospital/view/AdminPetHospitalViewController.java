package com.PetSitter.controller.Admin.hospital.view;

import com.PetSitter.domain.Member.MemberDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/pets-care/admin/pet-hospital")
public class AdminPetHospitalViewController {

    /**
     * 관리자 페이지 - 전국 동물 병원 CSV 파일 업로드 페이지
     */
    @GetMapping
    public String showPetHospitalUpload(@AuthenticationPrincipal MemberDetails memberDetails, Model model) {
        if (memberDetails != null && memberDetails.getMember() != null) {
            model.addAttribute("currentUser", memberDetails.getMember());
            model.addAttribute("isLogin", memberDetails.getMember().getId());
        }
        return "admin/hospital/pet-hospital-upload";
    }
}
