package com.PetSitter.controller.Admin.Reservation.view;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.MemberDetails;
import com.PetSitter.domain.Reservation.ReservationSearch;
import com.PetSitter.dto.Reservation.CustomerReservation.response.AdminReservationResponse;
import com.PetSitter.service.Admin.Reservation.AdminReservationService;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/pets-care/admin")
public class AdminReservationViewController {

    private final AdminReservationService adminReservationService;

    @Comment("관리자 페이지 모든 예약 목록 조회")
    @GetMapping("/reservations")
    public String getAllForAdmin(@ModelAttribute ReservationSearch reservationSearch, @AuthenticationPrincipal MemberDetails memberDetails,
                                 @PageableDefault Pageable pageable, Model model) {
        if (memberDetails.getMember() != null) {
            Page<AdminReservationResponse.ReservationListResponse> reservations = adminReservationService.findAllForAdmin(reservationSearch, memberDetails.getMember(), pageable);
            model.addAttribute("reservations", reservations);
            model.addAttribute("currentUser", memberDetails.getMember());
        }

        return "admin/reservation/reservation-list";
    }

    @Comment("관리자 페이지 예약 상세 정보 조회")
    @GetMapping("/reservations/{reservationId}")
    public String getForAdmin(@PathVariable("reservationId") long id, @AuthenticationPrincipal MemberDetails memberDetails, Model model) {
        if (memberDetails.getMember() != null) {
            AdminReservationResponse.ReservationDetailResponse reservation = adminReservationService.findForAdmin(id, memberDetails.getMember());
            model.addAttribute("reservation", reservation);
            model.addAttribute("currentUser", memberDetails.getMember());
        }

        return "admin/reservation/reservation-detail";
    }
}
