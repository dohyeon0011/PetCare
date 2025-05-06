package com.PetSitter.controller.Reservation.SitterSchedule.view;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.MemberDetails;
import com.PetSitter.domain.Member.oauth.CustomOAuth2User;
import com.PetSitter.dto.Reservation.CustomerReservation.response.CustomerReservationResponse;
import com.PetSitter.dto.Reservation.SitterSchedule.response.SitterScheduleResponse;
import com.PetSitter.service.Reservation.SitterSchedule.SitterScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/pets-care")
public class SitterScheduleViewController {

    private final SitterScheduleService sitterScheduleService;

    @Operation(description = "돌봄사의 전체 돌봄 예약 목록 조회")
    @GetMapping("/members/{sitterId}/schedules")
    public String getAllSitterSchedule(@PathVariable("sitterId") long sitterId, @AuthenticationPrincipal Object principal, Model model, Pageable pageable) {
//        List<SitterScheduleResponse.GetList> schedules = sitterScheduleService.findAllById(sitterId);
        Member member;

        if (principal instanceof MemberDetails && ((MemberDetails) principal).getMember() != null) {   // 일반 폼 로그인 사용자의 경우
            member = ((MemberDetails) principal).getMember();
            model.addAttribute("currentUser", member);
        } else if (principal instanceof CustomOAuth2User && ((CustomOAuth2User) principal).getMember() != null) { // OAuth2 소셜 로그인 사용자의 경우
            member = ((CustomOAuth2User) principal).getMember();
            model.addAttribute("currentUser", member);
            model.addAttribute("isOAuthUser", true);
        }

        Page<SitterScheduleResponse.GetList> schedules = sitterScheduleService.findAllById(sitterId, pageable);
        model.addAttribute("schedules", schedules);

        return "schedule/schedule-list";
    }

    @Operation(description = "특정 돌봄사의 특정 돌봄 예약 상세 정보 조회")
    @GetMapping("/members/{sitterId}/schedules/{sitterScheduleId}")
    public String getSitterSchedule(@PathVariable("sitterId") long sitterId,
                                    @PathVariable("sitterScheduleId") long sitterScheduleId,
                                    @AuthenticationPrincipal Object principal, Model model) {
        Member member;

        if (principal instanceof MemberDetails && ((MemberDetails) principal).getMember() != null) {   // 일반 폼 로그인 사용자의 경우
            member = ((MemberDetails) principal).getMember();
            model.addAttribute("currentUser", member);
        } else if (principal instanceof CustomOAuth2User && ((CustomOAuth2User) principal).getMember() != null) { // OAuth2 소셜 로그인 사용자의 경우
            member = ((CustomOAuth2User) principal).getMember();
            model.addAttribute("currentUser", member);
            model.addAttribute("isOAuthUser", true);
        }

        SitterScheduleResponse.GetDetail schedule = sitterScheduleService.findById(sitterId, sitterScheduleId);
        model.addAttribute("schedule", schedule);

        return "schedule/schedule-detail";
    }

}
