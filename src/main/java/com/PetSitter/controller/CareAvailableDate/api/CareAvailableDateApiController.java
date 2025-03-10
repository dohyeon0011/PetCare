package com.PetSitter.controller.CareAvailableDate.api;

import com.PetSitter.domain.Member.MemberDetails;
import com.PetSitter.dto.CareAvailableDate.request.AddCareAvailableDateRequest;
import com.PetSitter.dto.CareAvailableDate.request.UpdateCareAvailableDateRequest;
import com.PetSitter.dto.CareAvailableDate.response.CareAvailableDateResponse;
import com.PetSitter.service.CareAvailableDate.CareAvailableDateService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.ObjectError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets-care/members")
public class CareAvailableDateApiController {

    private final CareAvailableDateService careAvailableDateService;

    @Operation(description = "돌봄 가능 일정 등록 API")
    @PostMapping("/{sitterId}/care-available-dates/new")
    public ResponseEntity<?> saveCareAvailability(@PathVariable("sitterId") long id,
                                                  @RequestBody @Valid AddCareAvailableDateRequest request,
                                                  @AuthenticationPrincipal MemberDetails memberDetails,
                                                  BindingResult result) {
//        CareAvailableDate careAvailableDate = careAvailableDateService.save(id, request);

        // 검증 오류가 있을 경우 에러 메시지를 리턴
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();

            result.getFieldErrors().forEach(error -> {
                errors.put(error.getField(), error.getDefaultMessage()); // 필드와 메시지를 매핑
            });

            return ResponseEntity.badRequest()
                    .body(errors);
        }

        if (memberDetails.getMember().getId() != id) {
            return ResponseEntity.badRequest()
                    .build();
        }

        CareAvailableDateResponse.GetDetail careAvailableDate = careAvailableDateService.save(id, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(careAvailableDate);
    }

    @Operation(description = "모든 회원의 등록한 모든 돌봄 일정 조회 API")
    @GetMapping("/care-available-dates")
    public ResponseEntity<List<CareAvailableDateResponse.GetList>> findAllCareAvailableDate() {
        List<CareAvailableDateResponse.GetList> careAvailableDateAll = careAvailableDateService.findAll();

        return ResponseEntity.ok()
                .body(careAvailableDateAll);
    }

    @Operation(description = "회원의 등록한 모든 돌봄 일정 조회 API")
    @GetMapping("/{sitterId}/care-available-dates")
    public ResponseEntity<Page<CareAvailableDateResponse.GetList>> findCareAvailableDateList(@PathVariable("sitterId") long id, Pageable pageable, @AuthenticationPrincipal MemberDetails memberDetails) {
        if (memberDetails.getMember().getId() != id) {
            return ResponseEntity.badRequest()
                    .build();
        }

        Page<CareAvailableDateResponse.GetList> sitterAvailableDateList = careAvailableDateService.findAllById(id, pageable);

        return ResponseEntity.ok()
                .body(sitterAvailableDateList);
    }

    @Operation(description = "회원의 등록한 돌봄 일정 상세 조회 API")
    @GetMapping("/{sitterId}/care-available-dates/{careAvailableDateId}")
    public ResponseEntity<CareAvailableDateResponse.GetDetail> findCareAvailableDateOne(@PathVariable("sitterId") long id,
                                                                             @PathVariable("careAvailableDateId") long careAvailableDateId,
                                                                                        @AuthenticationPrincipal MemberDetails memberDetails) {
        if (memberDetails.getMember().getId() != id) {
            return ResponseEntity.badRequest()
                    .build();
        }

        CareAvailableDateResponse.GetDetail sitterAvailableDate = careAvailableDateService.findById(id, careAvailableDateId);

        return ResponseEntity.ok()
                .body(sitterAvailableDate);
    }

    @Operation(description = "회원의 등록한 특정 돌봄 일정 삭제 API")
    @DeleteMapping("/{sitterId}/care-available-dates/{careAvailableDateId}")
    public ResponseEntity<Void> deleteCareAvailableDate(@PathVariable("sitterId") long id,
                                                       @PathVariable("careAvailableDateId") long careAvailableDateId,
                                                        @AuthenticationPrincipal MemberDetails memberDetails) {
        if (memberDetails.getMember().getId() != id) {
            return ResponseEntity.badRequest()
                    .build();
        }

        careAvailableDateService.delete(id, careAvailableDateId);

        return ResponseEntity.ok()
                .build();
    }

    @Operation(description = "회원의 등록한 특정 돌봄 일정 수정 API")
    @PutMapping("/{sitterId}/care-available-dates/{careAvailableDateId}")
    public ResponseEntity<?> updateCareAvailableDate(@PathVariable("sitterId") long id,
                                                                            @PathVariable("careAvailableDateId") long careAvailableDateId,
                                                                            @RequestBody @Valid UpdateCareAvailableDateRequest request,
                                                                            @AuthenticationPrincipal MemberDetails memberDetails,
                                                                            BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errorMessages = new HashMap<>();

            result.getFieldErrors().forEach(error -> {
                errorMessages.put(error.getField(), error.getDefaultMessage());
            });

            return ResponseEntity.badRequest()
                    .body(errorMessages);
        }

        if (memberDetails.getMember().getId() != id) {
            return ResponseEntity.badRequest()
                    .build();
        }

        CareAvailableDateResponse.GetDetail updateSitterAvailableDate = careAvailableDateService.update(id, careAvailableDateId, request);

        return ResponseEntity.ok()
                .body(updateSitterAvailableDate);
    }

}
