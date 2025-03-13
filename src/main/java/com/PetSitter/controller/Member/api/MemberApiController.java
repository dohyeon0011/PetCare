package com.PetSitter.controller.Member.api;

import com.PetSitter.config.exception.BadRequestCustom;
import com.PetSitter.domain.Member.MemberDetails;
import com.PetSitter.domain.UploadFile;
import com.PetSitter.dto.Member.request.AddMemberRequest;
import com.PetSitter.dto.Member.request.UpdateMemberRequest;
import com.PetSitter.service.FileUploadService;
import com.PetSitter.service.Member.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets-care/members")
public class MemberApiController {

    private final MemberService memberService;
    private final FileUploadService fileUploadService;

    @Operation(summary = "회원가입", description = "회원가입 API")
    @PostMapping(value = "/new", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveMember(@RequestPart(value = "profileImage", required = false) @Parameter(description = "업로드 할 프로필 사진", content = @Content(mediaType = "multipart/form-data")) MultipartFile profileImage,
                                        @RequestPart(value = "request") @Valid AddMemberRequest request, BindingResult result) {
        if (result.hasErrors()) {
            // 오류 메시지를 담아 반환
            Map<String, String> errorMessages = new HashMap<>();

            result.getAllErrors().forEach(error -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errorMessages.put(fieldName, errorMessage);

                // 오류를 서버 로그에 기록
                log.error("Field: {}, Error: {}", fieldName, errorMessage);
            });

            return ResponseEntity.badRequest()
                    .body(errorMessages);
        }

        // 프로필 이미지를 등록 했다면 파일 업로드 처리
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                UploadFile uploadFile = fileUploadService.uploadFile(profileImage, "profile");
                Object member = memberService.save(request, uploadFile);

                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(member);
            } catch (IOException e) {
                log.error("프로필 이미지 파일 업로드 실패: {}", e.getMessage());

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("프로필 이미지 파일 업로드에 실패했습니다.");
            }
        }

        // 프로필 이미지를 등록하지 않았을 때
        Object member = memberService.save(request, null);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(member);
    }

    @Operation(summary = "특정 회원 정보 조회", description = "특정 회원 정보 조회 API")
    @GetMapping("/{memberId}/myPage")
    public ResponseEntity<?> findMember(@PathVariable("memberId") @Parameter(required = true, description = "회원 고유 번호") long id, @AuthenticationPrincipal MemberDetails memberDetails) {
        if (memberDetails.getMember().getId() != id) {
            throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
        }
        Object member = memberService.findById(id);

        return ResponseEntity.ok()
                .body(member);
    }

    /*@Operation(description = "돌봄사만 전체 조회 API")
    @GetMapping("/sitters")
    public ResponseEntity<List<MemberResponse.GetSitter>> findAllSitter() {
        memberService.
    }*/

    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴 API")
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable("memberId") @Parameter(required = true, description = "회원 고유 번호") long id, @AuthenticationPrincipal MemberDetails memberDetails) {
        if (memberDetails.getMember().getId() != id) {
            throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
        }
        memberService.delete(id);

        return ResponseEntity.ok()
                .build();
    }

    @Operation(summary = "회원 정보 수정", description = "회원 정보 수정 API")
    @PutMapping(value = "/{memberId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateMember(@PathVariable("memberId") @Parameter(required = true, description = "회원 고유 번호") Long id,
                                          @RequestPart(value = "profileImage", required = false) @Parameter(description = "업로드 할 프로필 사진", content = @Content(mediaType = "multipart/form-data")) MultipartFile profileImage,
                                          @RequestPart(value = "request") @Valid UpdateMemberRequest request, BindingResult result,
                                          @AuthenticationPrincipal MemberDetails memberDetails) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();

            result.getFieldErrors().forEach(error -> {
                errors.put(error.getField(), error.getDefaultMessage());
            });


            return ResponseEntity.badRequest()
                    .body(errors);
        }

        if (memberDetails.getMember().getId() != id) {
            throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
        }

        // 프로필 이미지 파일을 등록 했다면
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                UploadFile uploadFile = fileUploadService.uploadFile(profileImage, "profile");
                Object updateMember = memberService.update(id, request, uploadFile);

                return ResponseEntity.ok()
                        .body(updateMember);
            } catch (IOException e) {
                log.error("프로필 이미지 파일 업로드 실패: {}", e.getMessage());

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("프로필 이미지 파일 업로드에 실패했습니다.");
            }
        }

        // 프로필 이미지를 등록하지 않았을 때
        Object updateMember = memberService.update(id, request, null);

        return ResponseEntity.ok()
                .body(updateMember);
    }

}
