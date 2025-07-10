package com.PetSitter.controller.Reservation.CustomerReservation.view;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.MemberDetails;
import com.PetSitter.domain.Member.oauth.CustomOAuth2User;
import com.PetSitter.dto.Reservation.CustomerReservation.response.CustomerReservationResponse;
import com.PetSitter.dto.Reservation.ReservationResponse;
import com.PetSitter.service.Reservation.CustomerReservation.CustomerReservationService;
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

@Controller
@RequiredArgsConstructor
@RequestMapping("/pets-care")
public class CustomerReservationViewController {

    private final CustomerReservationService customerReservationService;
    private final SitterReservationService sitterReservationService;

    @Operation(description = "회원 돌봄 예약 생성")
    @GetMapping("/members/{customerId}/sitters/{sitterId}/reservations/new")
    public String newReservation(@PathVariable("customerId") long customerId, @PathVariable("sitterId") long sitterId, Model model, @AuthenticationPrincipal Object principal) {
        Member member;

        if (principal instanceof MemberDetails && ((MemberDetails) principal).getMember() != null) {   // 일반 폼 로그인 사용자의 경우
            member = ((MemberDetails) principal).getMember();
            model.addAttribute("currentUser", member);
            model.addAttribute("isLogin", member.getId());
        } else if (principal instanceof CustomOAuth2User && ((CustomOAuth2User) principal).getMember() != null) { // OAuth2 소셜 로그인 사용자의 경우
            member = ((CustomOAuth2User) principal).getMember();
            model.addAttribute("currentUser", member);
            model.addAttribute("isOAuthUser", true);
            model.addAttribute("isLogin", member.getId());
        }

        ReservationResponse reservationInfo = sitterReservationService.getReservationDetails(customerId, sitterId);
        model.addAttribute("reservationInfo", reservationInfo);

        return "reservation/new-reservation";
    }

    @Operation(description = "회원의 모든 돌봄 예약 내역 조회")
    @GetMapping("/members/{customerId}/reservations")
    public String getAllReservation(@PathVariable("customerId") long customerId, Pageable pageable, @AuthenticationPrincipal Object principal, Model model) {
//        List<CustomerReservationResponse.GetList> reservations = customerReservationService.findAllById(customerId);

        Member member;
        if (principal instanceof MemberDetails && ((MemberDetails) principal).getMember() != null) {   // 일반 폼 로그인 사용자의 경우
            member = ((MemberDetails) principal).getMember();
            model.addAttribute("currentUser", member);
            model.addAttribute("isLogin", member.getId());
        } else if (principal instanceof CustomOAuth2User && ((CustomOAuth2User) principal).getMember() != null) { // OAuth2 소셜 로그인 사용자의 경우
            member = ((CustomOAuth2User) principal).getMember();
            model.addAttribute("currentUser", member);
            model.addAttribute("isOAuthUser", true);
            model.addAttribute("isLogin", member.getId());
        }
        Page<CustomerReservationResponse.GetList> reservations = customerReservationService.findAllById(customerId, pageable);
        Long totalReservationAmount = customerReservationService.getTotalReservationAmount(customerId);

        model.addAttribute("reservations", reservations);
        model.addAttribute("totalReservationAmount", totalReservationAmount);

        return "reservation/reservation-list";
    }

    @Operation(description = "회원의 특정 돌봄 예약 상세 조회")
    @GetMapping("/members/{customerId}/reservations/{customerReservationId}")
    public String getReservation(@PathVariable("customerId") long customerId,
                                 @PathVariable("customerReservationId") long customerReservationId,
                                 @AuthenticationPrincipal Object principal, Model model) {
        Member member;

        if (principal instanceof MemberDetails && ((MemberDetails) principal).getMember() != null) {   // 일반 폼 로그인 사용자의 경우
            member = ((MemberDetails) principal).getMember();
            model.addAttribute("currentUser", member);
            model.addAttribute("isLogin", member.getId());
        } else if (principal instanceof CustomOAuth2User && ((CustomOAuth2User) principal).getMember() != null) { // OAuth2 소셜 로그인 사용자의 경우
            member = ((CustomOAuth2User) principal).getMember();
            model.addAttribute("currentUser", member);
            model.addAttribute("isOAuthUser", true);
            model.addAttribute("isLogin", member.getId());
        }

        CustomerReservationResponse.GetDetail reservation = customerReservationService.findById(customerId, customerReservationId);
        model.addAttribute("reservation", reservation);

        return "reservation/reservation-detail";
    }
}
