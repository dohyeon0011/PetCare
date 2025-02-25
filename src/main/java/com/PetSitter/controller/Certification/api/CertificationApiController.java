package com.PetSitter.controller.Certification.api;

import com.PetSitter.domain.Certification.Certification;
import com.PetSitter.dto.Certification.request.AddCertificationRequest;
import com.PetSitter.dto.Certification.request.UpdateCertificationRequest;
import com.PetSitter.dto.Certification.response.CertificationResponse;
import com.PetSitter.service.Certification.CertificationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Operation(description = "회원의 자격증 추가 API")
    @PostMapping("/{sitterId}/certifications/new")
    public ResponseEntity<?> addCertification(@PathVariable("sitterId") long id, @RequestBody @Valid List<AddCertificationRequest> request,
                                                                BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();

            result.getFieldErrors().forEach(error -> {
                errors.put(error.getField(), error.getDefaultMessage());
                log.error("Filed: {}, Message: {}", error.getField(), error.getDefaultMessage());
            });

            return ResponseEntity.badRequest()
                    .body(errors);
        }

        List<Certification> certifications = certificationService.save(id, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(certifications);
    }

    @Operation(description = "회원의 모든 자격증 조회 API")
    @GetMapping("/{sitterId}/certifications")
    public ResponseEntity<List<CertificationResponse.GetList>> findCertifications(@PathVariable("sitterId") long id) {
        List<CertificationResponse.GetList> certifications = certificationService.findById(id);

        return ResponseEntity.ok()
                .body(certifications);
    }

    @Operation(description = "회원의 특정 자격증 삭제 API")
    @DeleteMapping("/{sitterId}/certifications/{certificationId}")
    public ResponseEntity<Void> deleteCertification(@PathVariable("sitterId") long id, @PathVariable("certificationId") long certificationId) {
        certificationService.delete(id, certificationId);

        return ResponseEntity.ok()
                .build();
    }

    @Operation(description = "회원의 자격증 정보 수정 API")
    @PutMapping("/{sitterId}/certifications")
    public ResponseEntity<?> updateCertification(@PathVariable("sitterId") long id, @RequestBody @Valid List<UpdateCertificationRequest> requests,
                                                                                   BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();

            result.getFieldErrors().forEach(error -> {
                errors.put(error.getField(), error.getDefaultMessage());
                log.error("Filed: {}, Message: {}", error.getField(), error.getDefaultMessage());
            });

            return ResponseEntity.badRequest()
                    .body(errors);
        }

        List<CertificationResponse.GetList> updateCertifications = certificationService.update(id, requests);

        return ResponseEntity.ok()
                .body(updateCertifications);
    }

}
