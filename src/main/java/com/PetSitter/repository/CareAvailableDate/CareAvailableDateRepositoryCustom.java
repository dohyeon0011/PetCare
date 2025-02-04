package com.PetSitter.repository.CareAvailableDate;

import com.PetSitter.domain.CareAvailableDate.CareAvailableDate;
import com.PetSitter.domain.Member.Member;
import com.PetSitter.dto.Reservation.ReservationSitterResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface CareAvailableDateRepositoryCustom {

    // 돌봄 예약 가능 날짜를 등록한 회원 중 현재 예약이 가능한 회원만 조회(+엔티티로 조회 후 DTO 변환)
    Set<Member> findDistinctSitters();

    // 돌봄 예약 가능 날짜를 등록한 회원 중 현재 예약이 가능한 회원만 조회(+DTO로 직접 조회)
//    Set<ReservationSitterResponse.GetList> findDistinctSitterDetail();

    // 돌봄 예약 가능 날짜를 등록한 회원 중 현재 예약이 가능한 회원만 조회(+DTO로 직접 조회, 페이징)
    Page<ReservationSitterResponse.GetList> findDistinctSitterDetail(Pageable pageable);

    // 특정 돌봄사의 돌봄 예약 가능한 날짜만을 조회
    List<CareAvailableDate> findBySitterIdAndPossibility(long sitterId);
}
