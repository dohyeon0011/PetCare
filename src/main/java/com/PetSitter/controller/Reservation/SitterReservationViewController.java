package com.PetSitter.controller.Reservation;

import com.PetSitter.domain.Member.MemberDetails;
import com.PetSitter.dto.Reservation.ReservationSitterResponse;
import com.PetSitter.service.Reservation.SitterReservationService;
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
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/pets-care")
public class SitterReservationViewController {

    private final SitterReservationService sitterReservationService;

    @Operation(description = "고객에게 돌봄 예약 가능한 돌봄사들의 정보 조회")
    @GetMapping("/reservable-list")
    public String getAllReservable(@AuthenticationPrincipal MemberDetails memberDetails, Pageable pageable, Model model) {
//        List<ReservationSitterResponse.GetList> reservableSitters = sitterReservationService.findReservableSitters();
        if (memberDetails != null) {
            Page<ReservationSitterResponse.GetList> reservableSitters = sitterReservationService.findReservableSitters(pageable);
            model.addAttribute("reservableSitters", reservableSitters);
            model.addAttribute("currentUser", memberDetails.getMember());
        }

        return "reservable/reservable-list";
    }

    @Operation(description = "돌봄 예약 가능 목록 중 선택한 돌봄사의 자세한 정보 조회")
    @GetMapping("/reservable/members/{sitterId}")
    public String getReservableSitter(@PathVariable("sitterId") long sitterId, @RequestParam(defaultValue = "0") int page, @AuthenticationPrincipal MemberDetails memberDetails, Model model) {
        if (memberDetails != null) {
            ReservationSitterResponse.GetDetail reservableSitter = sitterReservationService.findReservableSitter(sitterId, page);
            model.addAttribute("reservableSitter", reservableSitter);
            model.addAttribute("currentUser", memberDetails.getMember());
        }

        return "reservable/reservable-detail";
    }

}
