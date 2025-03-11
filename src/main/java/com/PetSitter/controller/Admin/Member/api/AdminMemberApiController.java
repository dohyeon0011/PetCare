package com.PetSitter.controller.Admin.Member.api;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.MemberDetails;
import com.PetSitter.domain.Member.MemberSearch;
import com.PetSitter.dto.Member.response.AdminMemberResponse;
import com.PetSitter.service.Admin.Member.AdminMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets-care/admin")
public class AdminMemberApiController {

    private final AdminMemberService adminMemberService;

    @Operation(summary = "관리자 페이지 - 모든 회원 목록 조회", description = "관리자 권한 - 모든 회원 목록 조회 API")
    @GetMapping("/members")
    public ResponseEntity<Page<AdminMemberResponse.MemberListResponse>> getAllMember(@AuthenticationPrincipal MemberDetails memberDetails,
                                                                                     @ModelAttribute @Parameter(description = "검색 파라미터: 회원(고객) 이름 ex)\"\" 전체 조회") MemberSearch memberSearch,
                                                                                     @PageableDefault @Parameter(description = "페이징 파라미터, page: 페이지 번호 - 0부터 시작, size: 한 페이지의 데이터 개수") Pageable pageable) {
        Page<AdminMemberResponse.MemberListResponse> members = adminMemberService.findAllForAdmin(memberDetails.getMember(), memberSearch, pageable);

        return ResponseEntity.ok()
                .body(members);
    }

    @Operation(summary = "관리자 페이지 - 회원 상세 정보 조회", description = "관리자 권한 - 회원 상세 정보 조회 API")
    @GetMapping("/members/{memberId}")
    public ResponseEntity<?> getMemberDetail(@PathVariable("memberId") @Parameter(required = true, description = "회원 고유 번호") long id, @AuthenticationPrincipal MemberDetails memberDetails) {
        Object findMember = adminMemberService.findByIdForAdmin(id, memberDetails.getMember());

        return ResponseEntity.ok()
                .body(findMember);
    }

    @Operation(summary = "관리자 페이지 - 회원 탈퇴", description = "관리자 권한 - 회원 탈퇴 API")
    @DeleteMapping("/members/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable("memberId") @Parameter(required = true, description = "회원 고유 번호") long id, @AuthenticationPrincipal MemberDetails memberDetails) {
        adminMemberService.deleteForAdmin(id, memberDetails.getMember());

        return ResponseEntity.noContent()
                .build();
    }

}
