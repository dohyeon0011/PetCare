package com.PetSitter.controller.CareLog.api;

import com.PetSitter.config.exception.BadRequestCustom;
import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.MemberDetails;
import com.PetSitter.domain.Member.oauth.CustomOAuth2User;
import com.PetSitter.dto.CareLog.request.AddCareLogRequest;
import com.PetSitter.dto.CareLog.request.UpdateCareLogRequest;
import com.PetSitter.dto.CareLog.response.CareLogResponse;
import com.PetSitter.service.CareLog.CareLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets-care")
public class CareLogApiController {

    private final CareLogService careLogService;

    @Operation(summary = "돌봄사 - 돌봄 케어 로그 작성", description = "돌봄 케어 로그 작성 API")
    @PostMapping(value = "/members/{sitterId}/schedules/{sitterScheduleId}/care-logs/new", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveCareLog(@PathVariable("sitterId") @Parameter(required = true, description = "회원(돌봄사) 고유 번호") long sitterId,
                                                                 @PathVariable("sitterScheduleId") @Parameter(required = true, description = "돌봄 일정 고유 번호") long sitterScheduleId,
                                                                 @RequestPart(value = "request") @Valid AddCareLogRequest request, BindingResult result,
                                                                 @RequestPart(value = "careLogImage", required = false) @Parameter(description = "등록할 케어 로그 이미지 파일", content = @Content(mediaType = "multipart/form-data")) MultipartFile careLogImage,
                                                                 @AuthenticationPrincipal Object principal) throws FileUploadException {
        if (careLogImage != null) {
            System.out.println("Uploaded file name: " + careLogImage.getOriginalFilename());
        }
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();

            result.getFieldErrors().forEach(error -> {
                errors.put(error.getField(), error.getDefaultMessage());
            });

            return ResponseEntity.badRequest()
                    .body(errors);
        }

        if (principal instanceof MemberDetails) {
            Member member = ((MemberDetails) principal).getMember();

            if (member.getId() != sitterId) {
                throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
            }
        } else if (principal instanceof CustomOAuth2User) {
            Member member = ((CustomOAuth2User) principal).getMember();

            if (member.getId() != sitterId) {
                throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
            }
        }
        CareLogResponse.GetDetail careLog = careLogService.save(sitterId, sitterScheduleId, request, careLogImage);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(careLog);
    }

    @Operation(summary = "돌봄사 - 작성한 모든 돌봄 케어 로그 조회", description = "작성한 모든 돌봄 케어 로그 조회 API")
    @GetMapping("/members/{sitterId}/care-logs")
    public ResponseEntity<Page<CareLogResponse.GetList>> findAllCareLog(@PathVariable("sitterId") @Parameter(required = true, description = "회원(돌봄사) 고유 번호") long sitterId,
                                                                        @Parameter(description = "페이징 파라미터, page: 페이지 번호 - 0부터 시작, size: 한 페이지의 데이터 개수") Pageable pageable,
                                                                        @AuthenticationPrincipal Object principal) {
        if (principal instanceof MemberDetails) {
            Member member = ((MemberDetails) principal).getMember();

            if (member.getId() != sitterId) {
                throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
            }
        } else if (principal instanceof CustomOAuth2User) {
            Member member = ((CustomOAuth2User) principal).getMember();

            if (member.getId() != sitterId) {
                throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
            }
        }
        Page<CareLogResponse.GetList> careLogList = careLogService.findAll(sitterId, pageable);

        return ResponseEntity.ok()
                .body(careLogList);
    }

    @Operation(summary = "돌봄사 - 특정 돌봄에 대해 작성한 돌봄 케어 로그 전체 조회", description = "특정 돌봄에 대해 작성한 돌봄 케어 로그 전체 조회 API")
    @GetMapping("/members/{sitterId}/schedules/{sitterScheduleId}/care-logs")
    public ResponseEntity<List<CareLogResponse.GetDetail>> findAllCareLogById(@PathVariable("sitterId") @Parameter(required = true, description = "회원(돌봄사) 고유 번호") long sitterId,
                                                                              @PathVariable("sitterScheduleId") @Parameter(required = true, description = "돌봄 일정 고유 번호") long sitterScheduleId,
                                                                              @AuthenticationPrincipal Object principal) {
        if (principal instanceof MemberDetails) {
            Member member = ((MemberDetails) principal).getMember();

            if (member.getId() != sitterId) {
                throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
            }
        } else if (principal instanceof CustomOAuth2User) {
            Member member = ((CustomOAuth2User) principal).getMember();

            if (member.getId() != sitterId) {
                throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
            }
        }
        List<CareLogResponse.GetDetail> careLogList = careLogService.findAllById(sitterId, sitterScheduleId);

        return ResponseEntity.ok()
                .body(careLogList);
    }

    @Operation(summary = "돌봄사 - 특정 돌봄에 대해 작성한 특정 돌봄 케어 로그 상세 조회", description = "특정 돌봄에 대해 작성한 특정 돌봄 케어 로그 상세 조회 API")
    @GetMapping("/members/{sitterId}/care-logs/{careLogId}")
    public ResponseEntity<CareLogResponse.GetDetail> findCareLog(@PathVariable("sitterId") @Parameter(required = true, description = "회원(돌봄사) 고유 번호") long sitterId,
                                                                 @PathVariable("careLogId") @Parameter(required = true, description = "돌봄 케어 로그 고유 번호") long careLogId,
                                                                 @AuthenticationPrincipal Object principal) {
        if (principal instanceof MemberDetails) {
            Member member = ((MemberDetails) principal).getMember();

            if (member.getId() != sitterId) {
                throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
            }
        } else if (principal instanceof CustomOAuth2User) {
            Member member = ((CustomOAuth2User) principal).getMember();

            if (member.getId() != sitterId) {
                throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
            }
        }
        CareLogResponse.GetDetail careLog = careLogService.findById(sitterId, careLogId);

        return ResponseEntity.ok()
                .body(careLog);
    }

    @Operation(summary = "돌봄사 - 특정 돌봄 케어 로그 삭제", description = "특정 돌봄 케어 로그 삭제 API")
    @DeleteMapping("/members/{sitterId}/care-logs/{careLogId}")
    public ResponseEntity<Void> deleteCareLog(@PathVariable("sitterId") @Parameter(required = true, description = "회원(돌봄사) 고유 번호") long sitterId,
                                              @PathVariable("careLogId") @Parameter(required = true, description = "돌봄 케어 로그 고유 번호") long careLogId,
                                              @AuthenticationPrincipal Object principal) {
        if (principal instanceof MemberDetails) {
            Member member = ((MemberDetails) principal).getMember();

            if (member.getId() != sitterId) {
                throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
            }
        } else if (principal instanceof CustomOAuth2User) {
            Member member = ((CustomOAuth2User) principal).getMember();

            if (member.getId() != sitterId) {
                throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
            }
        }
        careLogService.delete(sitterId, careLogId);

        return ResponseEntity.noContent() // 204 No content 응답(백엔드가 204 No Content를 반환하는 경우 프론트엔드 json() 파싱에서 오류 발생 가능)
                .build();
    }

    @Operation(summary = "돌봄사 - 특정 돌봄 케어 로그 수정", description = "특정 돌봄 케어 로그 수정 API")
    @PutMapping(value = "/members/{sitterId}/care-logs/{careLogId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CareLogResponse.GetDetail> updateCareLog(@PathVariable("sitterId") @Parameter(required = true, description = "회원(돌봄사) 고유 번호") long sitterId,
                                                                   @PathVariable("careLogId") @Parameter(required = true, description = "돌봄 케어 로그 고유 번호") long careLogId,
                                                                   @RequestPart(value = "request") @Valid UpdateCareLogRequest request,
                                                                   @RequestPart(value = "careLogImage", required = false) @Parameter(description = "등록할 케어 로그 이미지 파일", content = @Content(mediaType = "multipart/form-data")) MultipartFile careLogImage,
                                                                   @AuthenticationPrincipal Object principal) throws FileUploadException {
        if (principal instanceof MemberDetails) {
            Member member = ((MemberDetails) principal).getMember();

            if (member.getId() != sitterId) {
                throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
            }
        } else if (principal instanceof CustomOAuth2User) {
            Member member = ((CustomOAuth2User) principal).getMember();

            if (member.getId() != sitterId) {
                throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
            }
        }
        CareLogResponse.GetDetail careLog = careLogService.update(sitterId, careLogId, request, careLogImage);

        return ResponseEntity.ok()
                .body(careLog);
    }
}
