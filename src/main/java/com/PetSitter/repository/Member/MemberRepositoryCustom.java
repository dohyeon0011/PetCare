package com.PetSitter.repository.Member;

import com.PetSitter.domain.CareAvailableDate.CareAvailableDate;
import com.PetSitter.domain.Pet.Pet;
import com.PetSitter.dto.Member.response.AdminMemberResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MemberRepositoryCustom {

    // 고객이 보유한 반려견 목록 전체 조회
    List<Pet> findPetsByCustomerId(long memberId);

    // 돌봄사가 등록한 예약 가능 날짜 전체 조회
    List<CareAvailableDate> findCareAvailableDatesBySitterId(long sitter);

    // 돌봄사가 등록한 예약 가능 날짜 중 특정 날짜 조회
    Optional<CareAvailableDate> findCareAvailableDateBySitterIdAndAvailableAt(long sitterId, LocalDate availableDate);

    // 관리자 페이지 모든 회원 목록 조회(+페이징)
    Page<AdminMemberResponse.MemberListResponse> findAllMember(Pageable pageable);
}
