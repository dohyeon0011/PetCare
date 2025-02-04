package com.PetSitter.controller.CareLog.api;

import com.PetSitter.dto.CareLog.request.AddCareLogRequest;
import com.PetSitter.dto.CareLog.request.UpdateCareLogRequest;
import com.PetSitter.dto.CareLog.response.CareLogResponse;
import com.PetSitter.service.CareLog.CareLogService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets-care")
public class CareLogApiController {

    private final CareLogService careLogService;

    @Operation(description = "케어 로그 작성 API")
    @PostMapping("/members/{sitterId}/schedules/{sitterScheduleId}/care-logs/new")
    public ResponseEntity<CareLogResponse.GetDetail> saveCareLog(@PathVariable("sitterId") long sitterId,
                                                                 @PathVariable("sitterScheduleId") long sitterScheduleId,
                                                                 @RequestBody @Valid AddCareLogRequest request) {
        CareLogResponse.GetDetail careLog = careLogService.save(sitterId, sitterScheduleId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(careLog);
    }

    @Operation(description = "돌봄사가 작성한 모든 돌봄 케어 로그 조회 API")
    @GetMapping("/members/{sitterId}/care-logs")
    public ResponseEntity<List<CareLogResponse.GetList>> findAllCareLog(@PathVariable("sitterId") long sitterId) {
        List<CareLogResponse.GetList> careLogList = careLogService.findAll(sitterId);

        return ResponseEntity.ok()
                .body(careLogList);
    }

    @Operation(description = "돌봄사가 특정 돌봄에 대해 작성한 돌봄 케어 로그 전체 조회 API")
    @GetMapping("/members/{sitterId}/schedules/{sitterScheduleId}/care-logs")
    public ResponseEntity<List<CareLogResponse.GetDetail>> findAllCareLogById(@PathVariable("sitterId") long sitterId,
                                                                              @PathVariable("sitterScheduleId") long sitterScheduleId) {
        List<CareLogResponse.GetDetail> careLogList = careLogService.findAllById(sitterId, sitterScheduleId);

        return ResponseEntity.ok()
                .body(careLogList);
    }

    @Operation(description = "돌봄사가 특정 돌봄에 대해 작성한 특정 돌봄 케어 로그 상세 조회 API")
    @GetMapping("/members/{sitterId}/care-logs/{careLogId}")
    public ResponseEntity<CareLogResponse.GetDetail> findCareLog(@PathVariable("sitterId") long sitterId, @PathVariable("careLogId") long careLogId) {
        CareLogResponse.GetDetail careLog = careLogService.findById(sitterId, careLogId);

        return ResponseEntity.ok()
                .body(careLog);
    }

    @Operation(description = "돌봄사의 특정 돌봄 케어 로그 삭제 API")
    @DeleteMapping("/members/{sitterId}/care-logs/{careLogId}")
    public ResponseEntity<Void> deleteCareLog(@PathVariable("sitterId") long sitterId, @PathVariable("careLogId") long careLogId) {
        careLogService.delete(sitterId, careLogId);

        return ResponseEntity.ok()
                .build();
    }

    @Operation(description = "돌봄사의 특정 돌봄 케어 로그 수정 API")
    @PutMapping("/members/{sitterId}/care-logs/{careLogId}")
    public ResponseEntity<CareLogResponse.GetDetail> updateCareLog(@PathVariable("sitterId") long sitterId,
                                                                   @PathVariable("careLogId") long careLogId,
                                                                   @RequestBody @Valid UpdateCareLogRequest request) {
        CareLogResponse.GetDetail careLog = careLogService.update(sitterId, careLogId, request);

        return ResponseEntity.ok()
                .body(careLog);
    }

}
