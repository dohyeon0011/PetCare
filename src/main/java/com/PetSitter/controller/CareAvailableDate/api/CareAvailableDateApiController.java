package com.PetSitter.controller.CareAvailableDate.api;

import com.PetSitter.config.exception.BadRequestCustom;
import com.PetSitter.domain.Member.MemberDetails;
import com.PetSitter.dto.CareAvailableDate.request.AddCareAvailableDateRequest;
import com.PetSitter.dto.CareAvailableDate.request.UpdateCareAvailableDateRequest;
import com.PetSitter.dto.CareAvailableDate.response.CareAvailableDateResponse;
import com.PetSitter.service.CareAvailableDate.CareAvailableDateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/pets-care/members")
public class CareAvailableDateApiController {

    private final CareAvailableDateService careAvailableDateService;

    @Operation(summary = "돌봄사 - 돌봄 가능 일정 등록", description = "돌봄 가능 일정 등록 API")
    @PostMapping("/{sitterId}/care-available-dates/new")
    public ResponseEntity<?> saveCareAvailability(@PathVariable("sitterId") @Parameter(required = true, description = "회원(돌봄사) 고유 번호") long id,
                                                  @AuthenticationPrincipal MemberDetails memberDetails,
                                                  @RequestBody @Valid AddCareAvailableDateRequest request,
                                                  BindingResult result) {
        // 검증 오류가 있을 경우 에러 메시지를 리턴
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();

            result.getFieldErrors().forEach(error -> {
                errors.put(error.getField(), error.getDefaultMessage()); // 필드와 메시지를 매핑
            });

            return ResponseEntity.badRequest()
                    .body(errors);
        }

        if (memberDetails != null && memberDetails.getMember().getId() != id) {
            throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
        }

        CareAvailableDateResponse.GetDetail careAvailableDate = careAvailableDateService.save(id, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(careAvailableDate);
    }

    /*@Operation(summary = "모든 회원의 등록한 모든 돌봄 일정 조회", description = "모든 회원의 등록한 모든 돌봄 일정 조회 API")
    @GetMapping("/care-available-dates")
    public ResponseEntity<List<CareAvailableDateResponse.GetList>> findAllCareAvailableDate() {
        List<CareAvailableDateResponse.GetList> careAvailableDateAll = careAvailableDateService.findAll();

        return ResponseEntity.ok()
                .body(careAvailableDateAll);
    }*/

    @Operation(summary = "돌봄사 - 등록한 모든 돌봄 일정 조회", description = "등록한 모든 돌봄 일정 조회 API")
    @GetMapping("/{sitterId}/care-available-dates")
    public ResponseEntity<Page<CareAvailableDateResponse.GetList>> findCareAvailableDateList(@PathVariable("sitterId") @Parameter(required = true, description = "회원(돌봄사) 고유 번호") long id,
                                                                                             @Parameter(description = "페이징 파라미터, page: 페이지 번호 - 0부터 시작, size: 한 페이지의 데이터 개수") Pageable pageable, @AuthenticationPrincipal MemberDetails memberDetails) {
        if (memberDetails != null && memberDetails.getMember().getId() != id) {
            throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
        }

        Page<CareAvailableDateResponse.GetList> sitterAvailableDateList = careAvailableDateService.findAllById(id, pageable);

        return ResponseEntity.ok()
                .body(sitterAvailableDateList);
    }

    @Operation(summary = "돌봄사 - 등록한 돌봄 일정 정보 상세 조회", description = "등록한 돌봄 일정 정보 상세 조회 API")
    @GetMapping("/{sitterId}/care-available-dates/{careAvailableDateId}")
    public ResponseEntity<CareAvailableDateResponse.GetDetail> findCareAvailableDateOne(@PathVariable("sitterId") @Parameter(required = true, description = "회원(돌봄사) 고유 번호") long id,
                                                                                        @PathVariable("careAvailableDateId") @Parameter(required = true, description = "돌봄 일정 고유 번호") long careAvailableDateId,
                                                                                        @AuthenticationPrincipal MemberDetails memberDetails) {
        if (memberDetails != null && memberDetails.getMember().getId() != id) {
            throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
        }

        CareAvailableDateResponse.GetDetail sitterAvailableDate = careAvailableDateService.findById(id, careAvailableDateId);

        return ResponseEntity.ok()
                .body(sitterAvailableDate);
    }

    @Operation(summary = "돌봄사 - 등록한 특정 돌봄 일정 삭제", description = "등록한 특정 돌봄 일정 삭제 API")
    @DeleteMapping("/{sitterId}/care-available-dates/{careAvailableDateId}")
    public ResponseEntity<Void> deleteCareAvailableDate(@PathVariable("sitterId") @Parameter(required = true, description = "회원(돌봄사) 고유 번호") long id,
                                                       @PathVariable("careAvailableDateId") @Parameter(required = true, description = "돌봄 일정 고유 번호") long careAvailableDateId,
                                                        @AuthenticationPrincipal MemberDetails memberDetails) {
        if (memberDetails != null && memberDetails.getMember().getId() != id) {
            throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
        }

        careAvailableDateService.delete(id, careAvailableDateId);

        return ResponseEntity.ok()
                .build();
    }

    @Operation(summary = "돌봄사 - 등록한 특정 돌봄 일정 수정", description = "등록한 특정 돌봄 일정 수정 API")
    @PutMapping("/{sitterId}/care-available-dates/{careAvailableDateId}")
    public ResponseEntity<?> updateCareAvailableDate(@PathVariable("sitterId") @Parameter(required = true, description = "회원(돌봄사) 고유 번호") long id,
                                                     @PathVariable("careAvailableDateId") @Parameter(required = true, description = "돌봄 일정 고유 번호") long careAvailableDateId,
                                                     @AuthenticationPrincipal MemberDetails memberDetails,
                                                     @RequestBody @Valid UpdateCareAvailableDateRequest request, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errorMessages = new HashMap<>();

            result.getFieldErrors().forEach(error -> {
                errorMessages.put(error.getField(), error.getDefaultMessage());
            });

            return ResponseEntity.badRequest()
                    .body(errorMessages);
        }

        if (memberDetails != null && memberDetails.getMember().getId() != id) {
            throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
        }

        CareAvailableDateResponse.GetDetail updateSitterAvailableDate = careAvailableDateService.update(id, careAvailableDateId, request);

        return ResponseEntity.ok()
                .body(updateSitterAvailableDate);
    }

}
