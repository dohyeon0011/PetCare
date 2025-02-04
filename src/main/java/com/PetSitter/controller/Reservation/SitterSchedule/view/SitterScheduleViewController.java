package com.PetSitter.controller.Reservation.SitterSchedule.view;

import com.PetSitter.dto.Reservation.SitterSchedule.response.SitterScheduleResponse;
import com.PetSitter.service.Reservation.SitterSchedule.SitterScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public String getAllSitterSchedule(@PathVariable("sitterId") long sitterId, Pageable pageable, Model model) {
//        List<SitterScheduleResponse.GetList> schedules = sitterScheduleService.findAllById(sitterId);
        Page<SitterScheduleResponse.GetList> schedules = sitterScheduleService.findAllById(sitterId, pageable);
        model.addAttribute("schedules", schedules);

        return "schedule/scheduleList";
    }

    @Operation(description = "특정 돌봄사의 특정 돌봄 예약 정보 조회 API")
    @GetMapping("/members/{sitterId}/schedules/{sitterScheduleId}")
    public String getSitterSchedule(@PathVariable("sitterId") long sitterId,
                                    @PathVariable("sitterScheduleId") long sitterScheduleId, Model model) {
        SitterScheduleResponse.GetDetail schedule = sitterScheduleService.findById(sitterId, sitterScheduleId);
        model.addAttribute("schedule", schedule);

        return "schedule/scheduleDetail";
    }

}
