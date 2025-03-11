package com.PetSitter.controller.Certification.api;

import com.PetSitter.config.exception.BadRequestCustom;
import com.PetSitter.domain.Certification.Certification;
import com.PetSitter.domain.Member.MemberDetails;
import com.PetSitter.dto.Certification.request.AddCertificationRequest;
import com.PetSitter.dto.Certification.request.UpdateCertificationRequest;
import com.PetSitter.dto.Certification.response.CertificationResponse;
import com.PetSitter.service.Certification.CertificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets-care/members")
@Slf4j
public class CertificationApiController {

    private final CertificationService certificationService;

    @Operation(summary = "돌봄사 - 자격증 추가", description = "자격증 추가 API")
    @PostMapping("/{sitterId}/certifications/new")
    public ResponseEntity<?> addCertification(@PathVariable("sitterId") @Parameter(required = true, description = "회원(돌봄사) 고유 번호") long id,
                                              @RequestBody @Valid List<AddCertificationRequest> request,
                                              @AuthenticationPrincipal MemberDetails memberDetails, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();

            result.getFieldErrors().forEach(error -> {
                errors.put(error.getField(), error.getDefaultMessage());
                log.error("Filed: {}, Message: {}", error.getField(), error.getDefaultMessage());
            });

            return ResponseEntity.badRequest()
                    .body(errors);
        }

        if (memberDetails != null && memberDetails.getMember().getId() != id) {
            throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
        }

        List<Certification> certifications = certificationService.save(id, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(certifications);
    }

    @Operation(summary = "돌봄사 - 모든 자격증 조회", description = "모든 자격증 조회 API")
    @GetMapping("/{sitterId}/certifications")
    public ResponseEntity<List<CertificationResponse.GetList>> findCertifications(@PathVariable("sitterId") @Parameter(required = true, description = "회원(돌봄사) 고유 번호") long id,
                                                                                  @AuthenticationPrincipal MemberDetails memberDetails) {
        if (memberDetails != null && memberDetails.getMember().getId() != id) {
            throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
        }

        List<CertificationResponse.GetList> certifications = certificationService.findById(id);

        return ResponseEntity.ok()
                .body(certifications);
    }

    @Operation(summary = "돌봄사 - 특정 자격증 삭제", description = "특정 자격증 삭제 API")
    @DeleteMapping("/{sitterId}/certifications/{certificationId}")
    public ResponseEntity<Void> deleteCertification(@PathVariable("sitterId") @Parameter(required = true, description = "회원(돌봄사) 고유 번호") long id,
                                                    @PathVariable("certificationId") @Parameter(required = true, description = "자격증 고유 번호") long certificationId, @AuthenticationPrincipal MemberDetails memberDetails) {
        if (memberDetails != null && memberDetails.getMember().getId() != id) {
            throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
        }

        certificationService.delete(id, certificationId);

        return ResponseEntity.noContent()
                .build();
    }

    @Operation(summary = "돌봄사 - 자격증 정보 수정", description = "자격증 정보 수정 API")
    @PutMapping("/{sitterId}/certifications")
    public ResponseEntity<?> updateCertification(@PathVariable("sitterId") @Parameter(required = true, description = "회원(돌봄사) 고유 번호") long id, @RequestBody @Valid List<UpdateCertificationRequest> requests,
                                                                                   @AuthenticationPrincipal MemberDetails memberDetails, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();

            result.getFieldErrors().forEach(error -> {
                errors.put(error.getField(), error.getDefaultMessage());
                log.error("Filed: {}, Message: {}", error.getField(), error.getDefaultMessage());
            });

            return ResponseEntity.badRequest()
                    .body(errors);
        }

        if (memberDetails != null && memberDetails.getMember().getId() != id) {
            throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
        }

        List<CertificationResponse.GetList> updateCertifications = certificationService.update(id, requests);

        return ResponseEntity.ok()
                .body(updateCertifications);
    }

}
