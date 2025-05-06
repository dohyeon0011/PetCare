package com.PetSitter.controller.Pet.view;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.MemberDetails;
import com.PetSitter.domain.Member.oauth.CustomOAuth2User;
import com.PetSitter.dto.Pet.response.PetResponse;
import com.PetSitter.service.Pet.PetService;
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
public class PetViewController {

    private final PetService petService;

    @Comment("반려견 등록")
    @GetMapping("/pets/new")
    public String newPet(@AuthenticationPrincipal Object principal, Model model) {
        Member member;

        if (principal instanceof MemberDetails && ((MemberDetails) principal).getMember() != null) {   // 일반 폼 로그인 사용자의 경우
            member = ((MemberDetails) principal).getMember();
            model.addAttribute("currentUser", member);
        } else if (principal instanceof CustomOAuth2User && ((CustomOAuth2User) principal).getMember() != null) { // OAuth2 소셜 로그인 사용자의 경우
            member = ((CustomOAuth2User) principal).getMember();
            model.addAttribute("currentUser", member);
            model.addAttribute("isOAuthUser", true);
        }

        return "pet/new-pet";
    }

    @Comment("반려견 수정")
    @GetMapping("/members/{customerId}/pets/edit")
    public String editPet(@PathVariable("customerId") long customerId, @AuthenticationPrincipal Object principal, Model model) {
        Member member;

        if (principal instanceof MemberDetails && ((MemberDetails) principal).getMember() != null) {   // 일반 폼 로그인 사용자의 경우
            member = ((MemberDetails) principal).getMember();
            model.addAttribute("currentUser", member);
        } else if (principal instanceof CustomOAuth2User && ((CustomOAuth2User) principal).getMember() != null) { // OAuth2 소셜 로그인 사용자의 경우
            member = ((CustomOAuth2User) principal).getMember();
            model.addAttribute("currentUser", member);
            model.addAttribute("isOAuthUser", true);
        }

        List<PetResponse.GetList> pets = petService.findById(customerId);
        model.addAttribute("pets", pets);

        return "pet/edit-pet";
    }

    /*@Comment("특정 회원의 모든 반려견 조회")
    @GetMapping("/members/{customerId}/pets")
    public String getAllPet(@PathVariable("customerId") long customerId, Model model) {
        List<PetResponse.GetList> pets = petService.findById(customerId);
        model.addAttribute("pets", pets);

        return "pet/pet-list";
    }*/

}
