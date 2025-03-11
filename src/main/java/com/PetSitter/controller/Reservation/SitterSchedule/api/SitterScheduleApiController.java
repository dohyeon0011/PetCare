package com.PetSitter.controller.Reservation.SitterSchedule.api;

import com.PetSitter.config.exception.BadRequestCustom;
import com.PetSitter.domain.Member.MemberDetails;
import com.PetSitter.dto.Reservation.SitterSchedule.response.SitterScheduleResponse;
import com.PetSitter.service.Reservation.SitterSchedule.SitterScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets-care")
public class SitterScheduleApiController {

    private final SitterScheduleService sitterScheduleService;

    @Operation(summary = "돌봄사 - 전체 돌봄 예약 목록 조회", description = "전체 돌봄 예약 목록 조회 API")
    @GetMapping("/members/{sitterId}/schedules")
    public ResponseEntity<Page<SitterScheduleResponse.GetList>> findAllSitterSchedule(@PathVariable("sitterId") @Parameter(required = true, description = "회원(돌봄사) 고유 번호") long id,
                                                                                      @Parameter(description = "페이징 파라미터, page: 페이지 번호 - 0부터 시작, size: 한 페이지의 데이터 개수") Pageable pageable,
                                                                                      @AuthenticationPrincipal MemberDetails memberDetails) {
//        List<SitterScheduleResponse.GetList> sitterScheduleList = sitterScheduleService.findAllById(id);

        if (memberDetails != null && memberDetails.getMember().getId() != id) {
            throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
        }

        Page<SitterScheduleResponse.GetList> sitterScheduleList = sitterScheduleService.findAllById(id, pageable);

        return ResponseEntity.ok()
                .body(sitterScheduleList);
    }

    @Operation(summary = "돌봄사 - 특정 돌봄 예약 정보 조회", description = "특정 돌봄 예약 정보 조회 API")
    @GetMapping("/members/{sitterId}/schedules/{sitterScheduleId}")
    public ResponseEntity<SitterScheduleResponse.GetDetail> findSitterSchedule(@PathVariable("sitterId") @Parameter(required = true, description = "회원(돌봄사) 고유 번호") long id,
                                                                               @PathVariable("sitterScheduleId") @Parameter (required = true, description = "예약 고유 번호") long sitterScheduleId,
                                                                               @AuthenticationPrincipal MemberDetails memberDetails) {
        if (memberDetails.getMember().getId() != id) {
            throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
        }

        SitterScheduleResponse.GetDetail sitterSchedule = sitterScheduleService.findById(id, sitterScheduleId);

        return ResponseEntity.ok()
                .body(sitterSchedule);
    }

    @Operation(summary = "돌봄사 - 특정 돌봄 예약 취소", description = "특정 돌봄 예약 취소 API")
    @DeleteMapping("/members/{sitterId}/schedules/{sitterScheduleId}")
    public ResponseEntity<Void> deleteSitterSchedule(@PathVariable("sitterId") @Parameter(required = true, description = "회원(돌봄사) 고유 번호") long id,
                                                     @PathVariable("sitterScheduleId") @Parameter(required = true, description = "예약 고유 번호") long sitterScheduleId,
                                                     @AuthenticationPrincipal MemberDetails memberDetails) {
        if (memberDetails.getMember().getId() != id) {
            throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
        }

        sitterScheduleService.delete(id, sitterScheduleId);

        return ResponseEntity.ok()
                .build();
    }

}
