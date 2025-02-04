package com.PetSitter.controller.Reservation.CustomerReservation.api;

import com.PetSitter.dto.Reservation.CustomerReservation.request.AddCustomerReservationRequest;
import com.PetSitter.dto.Reservation.CustomerReservation.response.CustomerReservationResponse;
import com.PetSitter.service.Reservation.CustomerReservation.CustomerReservationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets-care")
public class CustomerReservationApiController {

    private final CustomerReservationService customerReservationService;

    @Operation(description = "회원 돌봄 예약 생성 API")
    @PostMapping("/reservations/new")
    public ResponseEntity<CustomerReservationResponse.GetDetail> saveReservation(@RequestBody @Valid AddCustomerReservationRequest request) {
        CustomerReservationResponse.GetDetail customerReservation = customerReservationService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerReservation);
    }

    @Operation(description = "회원의 모든 돌봄 예약 내역 조회 API")
    @GetMapping("/members/{customerId}/reservations")
    public ResponseEntity<Page<CustomerReservationResponse.GetList>> findAllCustomerReservation(@PathVariable("customerId") long id, Pageable pageable) {
//        List<CustomerReservationResponse.GetList> customerReservationList = customerReservationService.findAllById(id, pageable);
        Page<CustomerReservationResponse.GetList> customerReservationList = customerReservationService.findAllById(id, pageable);

        return ResponseEntity.ok()
                .body(customerReservationList);
    }

    @Operation(description = "회원의 특정 돌봄 예약 상세 조회 API")
    @GetMapping("/members/{customerId}/reservations/{customerReservationId}")
    public ResponseEntity<CustomerReservationResponse.GetDetail> findCustomerReservation(@PathVariable("customerId") long id,
                                                                                            @PathVariable("customerReservationId") long customerReservationId) {
        CustomerReservationResponse.GetDetail customerReservation = customerReservationService.findById(id, customerReservationId);

        return ResponseEntity.ok()
                .body(customerReservation);
    }

    @Operation(description = "특정 회원의 특정 돌봄 예약 취소 API")
    @DeleteMapping("/members/{customerId}/reservations/{customerReservationId}")
    public ResponseEntity<Void> deleteCustomerReservation(@PathVariable("customerId") long id,
                                                          @PathVariable("customerReservationId") long customerReservationId) {
        customerReservationService.delete(id, customerReservationId);

        return ResponseEntity.ok()
                .build();
    }
}
