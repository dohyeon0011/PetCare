package com.PetSitter.controller.Member.api;

import com.PetSitter.domain.Member.MemberDetails;
import com.PetSitter.dto.Member.request.AddMemberRequest;
import com.PetSitter.dto.Member.request.UpdateMemberRequest;
import com.PetSitter.service.Member.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets-care/members")
public class MemberApiController {

    private final MemberService memberService;

    @Operation(description = "회원가입 API")
    @PostMapping("/new")
    public ResponseEntity<?> saveMember(@RequestBody @Valid AddMemberRequest request, BindingResult result) {
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

        Object member = memberService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(member);
    }

    @Operation(description = "특정 회원 조회 API")
    @GetMapping("/{memberId}/myPage")
    public ResponseEntity<?> findMember(@PathVariable("memberId") long id, @AuthenticationPrincipal MemberDetails memberDetails) {
        if (memberDetails.getMember().getId() != id) {
            return ResponseEntity.badRequest()
                    .build();
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

    @Operation(description = "회원 탈퇴 API")
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable("memberId") long id, @AuthenticationPrincipal MemberDetails memberDetails) {
        if (memberDetails.getMember().getId() != id) {
            return ResponseEntity.badRequest()
                    .build();
        }
        memberService.delete(id);

        return ResponseEntity.ok()
                .build();
    }

    @Operation(description = "회원 정보 수정 API")
    @PutMapping("/{memberId}")
    public ResponseEntity<?> updateMember(@PathVariable("memberId") Long id, @RequestBody @Valid UpdateMemberRequest request,
                                               @AuthenticationPrincipal MemberDetails memberDetails, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();

            result.getFieldErrors().forEach(error -> {
                errors.put(error.getField(), error.getDefaultMessage());
            });


            return ResponseEntity.badRequest()
                    .body(errors);
        }

        if (memberDetails.getMember().getId() != id) {
            return ResponseEntity.badRequest()
                    .build();
        }

        Object updateMember = memberService.update(id, request);

        return ResponseEntity.ok()
                .body(updateMember);
    }

}
