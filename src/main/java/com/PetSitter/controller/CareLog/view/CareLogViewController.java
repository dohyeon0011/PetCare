package com.PetSitter.controller.CareLog.view;

import com.PetSitter.dto.CareLog.response.CareLogResponse;
import com.PetSitter.service.CareLog.CareLogService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
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
    public String newCareLog(@PathVariable("sitterScheduleId") long sitterScheduleId, Model model) {
        CareLogResponse.GetNewCareLog careLog = careLogService.getReservation(sitterScheduleId);
        model.addAttribute("careLog", careLog);

        return "careLog/newCareLog";
    }

    @Operation(description = "돌봄사가 작성한 모든 돌봄 케어 로그 조회")
    @GetMapping("/members/{sitterId}/care-logs")
    public String getAllCareLog(@PathVariable("sitterId") long sitterId, Model model) {
        List<CareLogResponse.GetList> careLogs = careLogService.findAll(sitterId);
        model.addAttribute("careLogs", careLogs);

        return "careLog/careLogList";
    }

    @Operation(description = "돌봄사가 특정 돌봄에 대해 작성한 돌봄 케어 로그 전체 조회")
    @GetMapping("/members/{sitterId}/schedule/{sitterScheduleId}/care-logs")
    public String getCareLogList(@PathVariable("sitterId") long sitterId,
                                 @PathVariable("sitterScheduleId") long sitterScheduleId, Model model) {
        List<CareLogResponse.GetDetail> careLogs = careLogService.findAllById(sitterId, sitterScheduleId);
        model.addAttribute("careLogs", careLogs);

        return "careLog/careLog";
    }

    @Operation(description = "돌봄사가 특정 돌봄에 대해 작성한 특정 돌봄 케어 로그 상세 조회")
    @GetMapping("/members/{sitterId}/care-logs/{careLogId}")
    public String getCareLog(@PathVariable("sitterId") long sitterId, @PathVariable("careLogId") long careLogId, Model model) {
        CareLogResponse.GetDetail careLog = careLogService.findById(sitterId, careLogId);
        model.addAttribute("careLog", careLog);

        return "careLog/careLogDetail";
    }

    @Operation(description = "돌봄사의 특정 돌봄 케어 로그 수정")
    @GetMapping("/members/{sitterId}/care-logs/{careLogId}/edit")
    public String editCareLog(@PathVariable("sitterId") long sitterId, @PathVariable("careLogId") long careLogId, Model model) {
        CareLogResponse.GetDetail careLog = careLogService.findById(sitterId, careLogId);
        model.addAttribute("careLog", careLog);

        return "careLog/editCareLog";
    }
}
