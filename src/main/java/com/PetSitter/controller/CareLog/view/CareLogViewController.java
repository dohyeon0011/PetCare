package com.PetSitter.controller.CareLog.view;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.MemberDetails;
import com.PetSitter.domain.Member.oauth.CustomOAuth2User;
import com.PetSitter.dto.CareLog.response.CareLogResponse;
import com.PetSitter.service.CareLog.CareLogService;
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

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/pets-care")
public class CareLogViewController {

    private final CareLogService careLogService;

    @Operation(description = "돌봄 케어 로그 작성")
    @GetMapping("/schedules/{sitterScheduleId}/care-logs/new")
    public String newCareLog(@PathVariable("sitterScheduleId") long sitterScheduleId, @AuthenticationPrincipal Object principal, Model model) {
        Member member;

        if (principal instanceof MemberDetails && ((MemberDetails) principal).getMember() != null) {   // 일반 폼 로그인 사용자의 경우
            member = ((MemberDetails) principal).getMember();
            model.addAttribute("currentUser", member);
        } else if (principal instanceof CustomOAuth2User && ((CustomOAuth2User) principal).getMember() != null) { // OAuth2 소셜 로그인 사용자의 경우
            member = ((CustomOAuth2User) principal).getMember();
            model.addAttribute("currentUser", member);
            model.addAttribute("isOAuthUser", true);
        }

        CareLogResponse.GetNewCareLog careLog = careLogService.getReservation(sitterScheduleId);
        model.addAttribute("careLog", careLog);

        return "carelog/new-carelog";
    }

    @Operation(description = "돌봄사가 작성한 모든 돌봄 케어 로그 조회")
    @GetMapping("/members/{sitterId}/care-logs")
    public String getAllCareLog(@PathVariable("sitterId") long sitterId, Pageable pageable, @AuthenticationPrincipal Object principal, Model model) {
        Member member;

        if (principal instanceof MemberDetails && ((MemberDetails) principal).getMember() != null) {   // 일반 폼 로그인 사용자의 경우
            member = ((MemberDetails) principal).getMember();
            model.addAttribute("currentUser", member);
        } else if (principal instanceof CustomOAuth2User && ((CustomOAuth2User) principal).getMember() != null) { // OAuth2 소셜 로그인 사용자의 경우
            member = ((CustomOAuth2User) principal).getMember();
            model.addAttribute("currentUser", member);
            model.addAttribute("isOAuthUser", true);
        }

        Page<CareLogResponse.GetList> careLogs = careLogService.findAll(sitterId, pageable);
        model.addAttribute("careLogs", careLogs);

        return "carelog/carelog-list";
    }

    /*@Operation(description = "돌봄사가 특정 돌봄에 대해 작성한 돌봄 케어 로그 전체 조회")
    @GetMapping("/members/{sitterId}/schedule/{sitterScheduleId}/care-logs")
    public String getCareLogList(@PathVariable("sitterId") long sitterId,
                                 @PathVariable("sitterScheduleId") long sitterScheduleId,
                                 @AuthenticationPrincipal MemberDetails memberDetails, Model model) {
        if (memberDetails.getMember().getId() == sitterId) {
            List<CareLogResponse.GetDetail> careLogs = careLogService.findAllById(sitterId, sitterScheduleId);
            model.addAttribute("careLogs", careLogs);
            model.addAttribute("currentUser", memberDetails.getMember());
        }

        return "carelog/carelog";
    }*/

    /*@Operation(description = "돌봄사가 특정 돌봄에 대해 작성한 특정 돌봄 케어 로그 상세 조회")
    @GetMapping("/members/{sitterId}/care-logs/{careLogId}")
    public String getCareLog(@PathVariable("sitterId") long sitterId, @PathVariable("careLogId") long careLogId, @AuthenticationPrincipal MemberDetails memberDetails, Model model) {
        if (memberDetails.getMember().getId() == sitterId) {
            CareLogResponse.GetDetail careLog = careLogService.findById(sitterId, careLogId);
            model.addAttribute("careLog", careLog);
            model.addAttribute("currentUser", memberDetails.getMember());
        }

        return "carelog/carelog-detail";
    }*/

    /*@Operation(description = "돌봄사의 특정 돌봄 케어 로그 수정")
    @GetMapping("/members/{sitterId}/care-logs/{careLogId}/edit")
    public String editCareLog(@PathVariable("sitterId") long sitterId, @PathVariable("careLogId") long careLogId,
                              @AuthenticationPrincipal MemberDetails memberDetails, Model model) {
        if (memberDetails.getMember().getId() == sitterId) {
            CareLogResponse.GetDetail careLog = careLogService.findById(sitterId, careLogId);
            model.addAttribute("careLog", careLog);
            model.addAttribute("currentUser", memberDetails.getMember());
        }

        return "carelog/edit-carelog";
    }*/
}
