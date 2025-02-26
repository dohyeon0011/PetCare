package com.PetSitter.controller.Reservation;

import com.PetSitter.dto.Reservation.ReservationResponse;
import com.PetSitter.dto.Reservation.ReservationSitterResponse;
import com.PetSitter.dto.Review.response.ReviewResponse;
import com.PetSitter.service.Reservation.SitterReservationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets-care")
public class SitterReservationApiController {

    private final SitterReservationService sitterReservationService;

    @Operation(description = "고객에게 돌봄 예약 가능한 돌봄사들의 정보 조회 API")
    @GetMapping("/reservable-list")
    public ResponseEntity<Page<ReservationSitterResponse.GetList>> findAllAvailableReservation(Pageable pageable) {
//        List<ReservationSitterResponse.GetList> reservableSitters = sitterReservationService.findReservableSitters();
        Page<ReservationSitterResponse.GetList> reservableSitters = sitterReservationService.findReservableSitters(pageable);

        return ResponseEntity.ok()
                .body(reservableSitters);
    }

    @Operation(description = "돌봄 예약 가능 목록 중 선택한 돌봄사의 자세한 정보 조회 API")
    @GetMapping("/reservable/members/{sitterId}")
    public ResponseEntity<ReservationSitterResponse.GetDetail> findReservationSitter(@PathVariable("sitterId") long sitterId, @RequestParam(defaultValue = "0") int page) {
        ReservationSitterResponse.GetDetail reservableSitter = sitterReservationService.findReservableSitter(sitterId, page);

        return ResponseEntity.ok()
                .body(reservableSitter);
    }

    @Operation(description = "고객이 예약할 때 보여줄 정보 API")
    @GetMapping("/members/{customerId}/sitters/{sitterId}/reservations")
    public ResponseEntity<ReservationResponse> getReservationInfo(@PathVariable("customerId") long customerId, @PathVariable("sitterId") long sitterId) {
        ReservationResponse reservationDetails = sitterReservationService.getReservationDetails(customerId, sitterId);

        return ResponseEntity.ok()
                .body(reservationDetails);
    }

    // 일반적인 List<>가 아닌, Map<>에 담아 뷰에 넘겨줄 경우, 한번에 여러 정보를 담아서 보내줄 수 있어서 좋음.
    @Operation(description = "선택한 돌봄사의 자세한 정보 중 리뷰 정보만 더 조회")
    @GetMapping("/reservable/members/{sitterId}/reviews")
    public ResponseEntity<Map<String, Object>> getMoreReviews(@PathVariable("sitterId") long sitterId, @RequestParam(defaultValue = "0") int page) {
        List<ReviewResponse.GetDetail> reviews = sitterReservationService.getReviewsBySitterId(sitterId, page, 5);
        long totalReviews = sitterReservationService.getTotalReviewsBySitterId(sitterId);

        Map<String, Object> response = new HashMap<>();
        response.put("reviews", reviews);
        response.put("totalReviews", totalReviews); // (Pageable을 사용하지 않아서 일일이 모델에 담아줘야함.)

        return ResponseEntity.ok()
                .body(response);
    }

}
