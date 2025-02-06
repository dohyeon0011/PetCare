package com.PetSitter.controller.CareAvailableDate.view;

import com.PetSitter.dto.CareAvailableDate.response.CareAvailableDateResponse;
import com.PetSitter.service.CareAvailableDate.CareAvailableDateService;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/pets-care")
public class CareAvailableDateViewController {

    private final CareAvailableDateService careAvailableDateService;

    @Comment("돌봄 가능 일정 등록")
    @GetMapping("/members/{sitterId}/care-available-dates/new")
    public String newCareAvailableDate(@PathVariable("sitterId") long sitterId, Model model) {
        model.addAttribute("careAvailableDate", new CareAvailableDateResponse.GetList());

        return "careavailabledate/new-care-available-date";
    }

    @Comment("회원의 등록한 돌봄 일정 상세 조회")
    @GetMapping("/members/care-available-dates")
    public String getAllCareAvailableDate(Model model) {
        List<CareAvailableDateResponse.GetList> careAvailableDates = careAvailableDateService.findAll();
        model.addAttribute("careAvailableDates", careAvailableDates);

        return "admin/careavailabledate/care-available-date-list";
    }

    @Comment("회원의 등록한 모든 돌봄 일정 조회")
    @GetMapping("/members/{sitterId}/care-available-dates")
    public String getCareAvailableDateList(@PathVariable("sitterId") long sitterId, Pageable pageable, Model model) {
        Page<CareAvailableDateResponse.GetList> careAvailableDates = careAvailableDateService.findAllById(sitterId, pageable);
        model.addAttribute("careAvailableDates", careAvailableDates);

        return "careavailabledate/care-available-date-list";
    }

    @Comment("회원의 등록한 돌봄 일정 상세 조회")
    @GetMapping("/members/{sitterId}/care-available-dates/{careAvailablaDateId}")
    public String getCareAvailableDate(@PathVariable("sitterId") long sitterId,
                                       @PathVariable("careAvailableDateId") long careAvailableDateId, Model model) {
        CareAvailableDateResponse.GetDetail careAvailableDate = careAvailableDateService.findById(sitterId, careAvailableDateId);
        model.addAttribute("careAvailableDate", careAvailableDate);

        return "careavailabledate/care-available-date-detail";
    }

    @Comment("회원의 등록한 돌봄 가능 일정 수정")
    @GetMapping("/members/{sitterId}/care-available-dates/{careAvailableDateId}/edit")
    public String editCareAvailableDate(@PathVariable("sitterId") long sitterId, @PathVariable("careAvailableDateId") long careAvailableDateId, Model model) {
        CareAvailableDateResponse.GetDetail careAvailableDate = careAvailableDateService.findById(sitterId, careAvailableDateId);
        model.addAttribute("careAvailableDate", careAvailableDate);

        return "careavailabledate/edit-care-available-date";
    }

}
