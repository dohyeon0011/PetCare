package com.PetSitter.controller.Admin.hospital.api;

import com.PetSitter.domain.Member.MemberDetails;
import com.PetSitter.service.Admin.hospital.AdminPetHospitalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets-care/admin/pet-hospitals")
@Slf4j
public class AdminPetHospitalApiController {

    private final AdminPetHospitalService adminPetHospitalService;

    @Operation(summary = "관리자 페이지 - 동물 병원 파일 업로드", description = "관리자 권한 - 동물 병원 파일 업로드 API")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadPetHospitalFromCsv(@RequestPart("file") @Parameter(description = "전국 동물병원 CSV 파일", content = @Content(mediaType = "multipart/form-data")) MultipartFile file,
                                                           @AuthenticationPrincipal MemberDetails memberDetails) {
        if (file.isEmpty() || !file.getOriginalFilename().endsWith(".csv")) {
            return ResponseEntity.badRequest()
                    .body("파일이 비어있거나, CSV 파일만 업로드 가능합니다.");
        }

        if (memberDetails == null || memberDetails.getMember() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("인증된 관리자만 파일을 업로드 할 수 있습니다.");
        }

        try {
            log.info("AdminPetHospitalApiController - uploadPetHospitalFromCsv(): try-catch 호출 성공.");
            adminPetHospitalService.saveFromCsv(file, memberDetails.getMember());
            return ResponseEntity.ok()
                    .body("CSV 업로드 및 병원 정보 반영 완료.");
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("파일 처리 중 오류 발생: " + e.getMessage());
        }
    }
}
