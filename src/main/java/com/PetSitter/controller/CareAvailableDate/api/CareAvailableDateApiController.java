package com.PetSitter.controller.CareAvailableDate.api;

import com.PetSitter.domain.CareAvailableDate.CareAvailableDate;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets-care/members")
public class CareAvailableDateApiController {

    private final CareAvailableDateService careAvailableDateService;

    @Operation(description = "돌봄 가능 일정 등록 API")
    @PostMapping("/{sitterId}/care-available-dates/new")
    public ResponseEntity<CareAvailableDateResponse.GetDetail> saveCareAvailability(@PathVariable("sitterId") long id,
                                                                  @RequestBody @Valid AddCareAvailableDateRequest request) {
//        CareAvailableDate careAvailableDate = careAvailableDateService.save(id, request);
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
    public ResponseEntity<Page<CareAvailableDateResponse.GetList>> findCareAvailableDateList(@PathVariable("sitterId") long id, Pageable pageable) {
        Page<CareAvailableDateResponse.GetList> sitterAvailableDateList = careAvailableDateService.findAllById(id, pageable);

        return ResponseEntity.ok()
                .body(sitterAvailableDateList);
    }

    @Operation(description = "회원의 등록한 돌봄 일정 상세 조회 API")
    @GetMapping("/{sitterId}/care-available-dates/{careAvailableDateId}")
    public ResponseEntity<CareAvailableDateResponse.GetDetail> findCareAvailableDateOne(@PathVariable("sitterId") long id,
                                                                             @PathVariable("careAvailableDateId") long careAvailableDateId) {
        CareAvailableDateResponse.GetDetail sitterAvailableDate = careAvailableDateService.findById(id, careAvailableDateId);

        return ResponseEntity.ok()
                .body(sitterAvailableDate);
    }

    @Operation(description = "회원의 등록한 특정 돌봄 일정 삭제 API")
    @DeleteMapping("/{sitterId}/care-available-dates/{careAvailableDateId}")
    public ResponseEntity<Void> deleteCareAvailableDate(@PathVariable("sitterId") long id,
                                                       @PathVariable("careAvailableDateId") long careAvailableDateId) {
        careAvailableDateService.delete(id, careAvailableDateId);

        return ResponseEntity.ok()
                .build();
    }

    @Operation(description = "회원의 등록한 특정 돌봄 일정 수정 API")
    @PutMapping("/{sitterId}/care-available-dates/{careAvailableDateId}")
    public ResponseEntity<CareAvailableDateResponse.GetDetail> updateCareAvailableDate(@PathVariable("sitterId") long id,
                                                                            @PathVariable("careAvailableDateId") long careAvailableDateId,
                                                                            @RequestBody @Valid UpdateCareAvailableDateRequest request) {
        CareAvailableDateResponse.GetDetail updateSitterAvailableDate = careAvailableDateService.update(id, careAvailableDateId, request);

        return ResponseEntity.ok()
                .body(updateSitterAvailableDate);
    }

}
