package com.PetSitter.controller.Admin.Reservation.api;

import com.PetSitter.domain.Reservation.ReservationSearch;
import com.PetSitter.dto.Reservation.CustomerReservation.response.AdminReservationResponse;
import com.PetSitter.service.Reservation.CustomerReservation.CustomerReservationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets-care/admin")
public class AdminReservationApiController {

    private final CustomerReservationService customerReservationService;

    @Operation(description = "관리자 페이지 모든 예약 목록 조회 API")
    @GetMapping("/reservations")
    public ResponseEntity<Page<AdminReservationResponse.ReservationListResponse>> findAllForAdmin(@ModelAttribute ReservationSearch reservationSearch, @PageableDefault Pageable pageable) {
        Page<AdminReservationResponse.ReservationListResponse> reservations = customerReservationService.findAllForAdmin(reservationSearch, pageable);

        return ResponseEntity.ok()
                .body(reservations);
    }

//    @Operation(description = "관리자 페이지 예약 상세 정보 조회 API")
//    @GetMapping("/reservations/{reservationId}")
//    public ResponseEntity<AdminReservationResponse.ReservationDetailResponse> findForAdmin(@PathVariable("reservationId") long id) {
//
//    }

}
