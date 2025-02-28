package com.PetSitter.controller.Admin.Member.api;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.MemberSearch;
import com.PetSitter.dto.Member.response.AdminMemberResponse;
import com.PetSitter.service.Member.AdminMemberService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets-care/admin")
public class AdminMemberApiController {

    private final AdminMemberService adminMemberService;

    @Operation(description = "관리자 페이지 모든 회원 목록 조회 API")
    @GetMapping("/members")
    public ResponseEntity<Page<AdminMemberResponse.MemberListResponse>> getAllMember(@SessionAttribute("member") Member member,
                                                                                     @ModelAttribute MemberSearch memberSearch,
                                                                                     @PageableDefault Pageable pageable) {
        Page<AdminMemberResponse.MemberListResponse> members = adminMemberService.findAllForAdmin(member, memberSearch, pageable);

        return ResponseEntity.ok()
                .body(members);
    }

    @Operation(description = "관리자 페이지 회원 상세 정보 조회 API")
    @GetMapping("/members/{memberId}")
    public ResponseEntity<?> getMemberDetail(@PathVariable("memberId") long id, @SessionAttribute("member") Member member) {
        Object findMember = adminMemberService.findByIdForAdmin(id, member);

        return ResponseEntity.ok()
                .body(findMember);
    }

    @Operation(description = "관리자 권한 회원 탈퇴 API")
    @DeleteMapping("/members/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable("memberId") long id, @SessionAttribute("member") Member member) {
        adminMemberService.deleteForAdmin(id, member);

        return ResponseEntity.noContent()
                .build();
    }

}
