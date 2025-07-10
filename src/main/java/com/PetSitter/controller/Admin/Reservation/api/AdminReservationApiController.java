package com.PetSitter.controller.Admin.Reservation.api;

import com.PetSitter.domain.Member.MemberDetails;
import com.PetSitter.domain.Reservation.ReservationSearch;
import com.PetSitter.dto.Reservation.CustomerReservation.response.AdminReservationResponse;
import com.PetSitter.service.Admin.Reservation.AdminReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets-care/admin")
public class AdminReservationApiController {

    private final AdminReservationService adminReservationService;

    @Operation(summary = "관리자 페이지 - 모든 예약 목록 조회", description = "관리자 권한 - 모든 예약 목록 조회 API")
    @GetMapping("/reservations")
    public ResponseEntity<Map<String, Object>> findAllForAdmin(@ModelAttribute @Parameter(description = "검색 파라미터: 회원(고객) 이름") ReservationSearch reservationSearch,
                                                                                                  @AuthenticationPrincipal MemberDetails memberDetails,
                                                                                                  @PageableDefault @Parameter(description = "페이징 파라미터, page: 페이지 번호 - 0부터 시작, size: 한 페이지의 데이터 개수") Pageable pageable) {
        if (memberDetails == null || memberDetails.getMember() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "error", "해당 기능은 관리자 기능으로 권한이 존재하지 않습니다."
                    ));
        }
        Page<AdminReservationResponse.ReservationListResponse> reservations = adminReservationService.findAllForAdmin(reservationSearch, memberDetails.getMember(), pageable);
        Long totalReservationAmount = adminReservationService.getTotalReservationAmount(memberDetails.getMember());

        Map<String, Object> response = Map.of(
                "reservations", reservations,
                "totalReservationAmount", totalReservationAmount
        );

        return ResponseEntity.ok()
                .body(response);
    }

    @Operation(summary = "관리자 페이지 - 예약 상세 정보 조회", description = "관리자 페이지 예약 상세 정보 조회 API")
    @GetMapping("/reservations/{reservationId}")
    public ResponseEntity<AdminReservationResponse.ReservationDetailResponse> findForAdmin(@PathVariable("reservationId") @Parameter(required = true, description = "예약 고유 번호") long id,
                                                                                           @AuthenticationPrincipal MemberDetails memberDetails) {
        AdminReservationResponse.ReservationDetailResponse reservation = adminReservationService.findForAdmin(id, memberDetails.getMember());

        return ResponseEntity.ok()
                .body(reservation);
    }

    @Operation(summary = "관리자 페이지 - 예약 취소", description = "관리자 권한 예약 취소 API")
    @DeleteMapping("reservations/{reservationId}")
    public ResponseEntity<Void> deleteForAdmin(@PathVariable("reservationId") @Parameter(required = true, description = "예약 고유 번호") long id, @AuthenticationPrincipal MemberDetails memberDetails) {
        adminReservationService.deleteForAdmin(id, memberDetails.getMember());

        return ResponseEntity.noContent()
                .build();
    }
}
