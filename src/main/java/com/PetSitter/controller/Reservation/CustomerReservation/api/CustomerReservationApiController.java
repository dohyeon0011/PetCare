package com.PetSitter.controller.Reservation.CustomerReservation.api;

import com.PetSitter.config.exception.BadRequestCustom;
import com.PetSitter.domain.Member.MemberDetails;
import com.PetSitter.dto.Reservation.CustomerReservation.request.AddCustomerReservationRequest;
import com.PetSitter.dto.Reservation.CustomerReservation.response.CustomerReservationResponse;
import com.PetSitter.service.Reservation.CustomerReservation.CustomerReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets-care")
@Slf4j
public class CustomerReservationApiController {

    private final CustomerReservationService customerReservationService;

    @Operation(summary = "고객 - 돌봄 예약 생성", description = "돌봄 예약 생성 API")
    @PostMapping("/reservations/new")
    public ResponseEntity<?> saveReservation(@RequestBody @Valid AddCustomerReservationRequest request, @AuthenticationPrincipal MemberDetails memberDetails, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();

            result.getFieldErrors().forEach(error -> {
                errors.put(error.getField(), error.getDefaultMessage());
                log.error("Field: {}, Error : {}", error.getField(), error.getDefaultMessage());
            });

            return ResponseEntity.badRequest()
                    .body(errors);
        }

        if (memberDetails != null && memberDetails.getMember().getId() != request.getCustomerId()) {
            throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
        }

        CustomerReservationResponse.GetDetail customerReservation = customerReservationService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerReservation);
    }

    @Operation(summary = "고객 - 모든 돌봄 예약 내역 조회", description = "모든 돌봄 예약 내역 조회 API")
    @GetMapping("/members/{customerId}/reservations")
    public ResponseEntity<Page<CustomerReservationResponse.GetList>> findAllCustomerReservation(@PathVariable("customerId") @Parameter(required = true, description = "회원(고객) 고유 번호") long id,
                                                                                                @Parameter(description = "페이징 파라미터, page: 페이지 번호 - 0부터 시작, size: 한 페이지의 데이터 개수") Pageable pageable,
                                                                                                @AuthenticationPrincipal MemberDetails memberDetails) {
//        List<CustomerReservationResponse.GetList> customerReservationList = customerReservationService.findAllById(id, pageable);

        if (memberDetails != null && memberDetails.getMember().getId() != id) {
            throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
        }

        Page<CustomerReservationResponse.GetList> customerReservationList = customerReservationService.findAllById(id, pageable);

        return ResponseEntity.ok()
                .body(customerReservationList);
    }

    @Operation(summary = "고객 - 특정 돌봄 예약 상세 조회", description = "특정 돌봄 예약 상세 조회 API")
    @GetMapping("/members/{customerId}/reservations/{customerReservationId}")
    public ResponseEntity<CustomerReservationResponse.GetDetail> findCustomerReservation(@PathVariable("customerId") @Parameter(required = true, description = "회원(고객) 고유 번호") long id,
                                                                                         @PathVariable("customerReservationId") @Parameter(required = true, description = "예약 고유 번호") long customerReservationId,
                                                                                         @AuthenticationPrincipal MemberDetails memberDetails) {
        if (memberDetails != null && memberDetails.getMember().getId() != id) {
            throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
        }

        CustomerReservationResponse.GetDetail customerReservation = customerReservationService.findById(id, customerReservationId);

        return ResponseEntity.ok()
                .body(customerReservation);
    }

    @Operation(summary = "고객 - 특정 돌봄 예약 취소", description = "특정 돌봄 예약 취소 API")
    @DeleteMapping("/members/{customerId}/reservations/{customerReservationId}")
    public ResponseEntity<Void> deleteCustomerReservation(@PathVariable("customerId") @Parameter(required = true, description = "회원(고객) 고유 번호") long id,
                                                          @PathVariable("customerReservationId") @Parameter(required = true, description = "예약 고유 번호") long customerReservationId,
                                                          @AuthenticationPrincipal MemberDetails memberDetails) {
        if (memberDetails != null && memberDetails.getMember().getId() != id) {
            throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
        }

        customerReservationService.delete(id, customerReservationId);

        return ResponseEntity.ok()
                .build();
    }
}
