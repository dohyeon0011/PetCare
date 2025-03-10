package com.PetSitter.controller.Pet.view;

import com.PetSitter.domain.Member.MemberDetails;
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
    public String newPet(@AuthenticationPrincipal MemberDetails memberDetails, Model model) {
        if (memberDetails.getMember() != null) {
            model.addAttribute("currentUser", memberDetails.getMember());
        }

        return "pet/new-pet";
    }

    @Comment("반려견 수정")
    @GetMapping("/members/{customerId}/pets/edit")
    public String editPet(@PathVariable("customerId") long customerId, @AuthenticationPrincipal MemberDetails memberDetails, Model model) {
        if (memberDetails.getMember().getId() == customerId) {
            List<PetResponse.GetList> pets = petService.findById(customerId);
            model.addAttribute("pets", pets);
            model.addAttribute("currentUser", memberDetails.getMember());
        }

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
