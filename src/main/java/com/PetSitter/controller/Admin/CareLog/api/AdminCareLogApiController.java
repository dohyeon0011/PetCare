package com.PetSitter.controller.Admin.CareLog.api;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.service.CareLog.AdminCareLogService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets-care/admin")
public class AdminCareLogApiController {

    private final AdminCareLogService adminCareLogService;

    @Operation(description = "관리자 권한 돌봄 케어 로그 삭제 API")
    @DeleteMapping("/care-logs/{careLogId}")
    public ResponseEntity<Void> deleteForAdmin(@PathVariable("careLogId") long id, @SessionAttribute("member") Member member) {
        adminCareLogService.deleteForAdmin(id, member);

        return ResponseEntity.noContent()
                .build();
    }
}
