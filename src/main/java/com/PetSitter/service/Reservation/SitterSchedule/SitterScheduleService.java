package com.PetSitter.service.Reservation.SitterSchedule;

import com.PetSitter.domain.CareAvailableDate.CareAvailableDate;
import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.Role;
import com.PetSitter.domain.Reservation.SitterSchedule.SitterSchedule;
import com.PetSitter.dto.Reservation.SitterSchedule.response.SitterScheduleResponse;
import com.PetSitter.repository.CareAvailableDate.CareAvailableDateRepository;
import com.PetSitter.repository.Member.MemberRepository;
import com.PetSitter.repository.Reservation.SitterSchedule.SitterScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class SitterScheduleService {

    private final MemberRepository memberRepository;
    private final SitterScheduleRepository sitterScheduleRepository;
    private final CareAvailableDateRepository careAvailableDateRepository;

    @Comment("특정 돌봄사의 전체 돌봄 예약 목록 조회")
    @Transactional(readOnly = true)
    public Page<SitterScheduleResponse.GetList> findAllById(long sitterId, Pageable pageable) {
        Member sitter = memberRepository.findById(sitterId)
                .orElseThrow(() -> new NoSuchElementException("돌봄 예약 조회 오류: 돌봄사 정보 조회에 실패했습니다."));

        verifyingPermissionsSitter(sitter);

//        List<SitterScheduleResponse.GetList> sitterSchedules = sitter.getSitterSchedules().stream()
//                .map(SitterScheduleResponse.GetList::new)
//                .toList();

//        List<SitterScheduleResponse.GetList> sitterSchedules = sitterScheduleRepository.findBySitterId(sitter.getId())
//                .stream()
//                .map(SitterScheduleResponse.GetList::new)
//                .toList();

        Page<SitterScheduleResponse.GetList> sitterSchedules = sitterScheduleRepository.findBySitterId(sitterId, pageable);

        return sitterSchedules;
    }

    @Comment("특정 돌봄사의 특정 돌봄 예약 정보 조회")
    @Transactional(readOnly = true)
    public SitterScheduleResponse.GetDetail findById(long sitterId, long sitterScheduleId) {
        SitterSchedule sitterSchedule = sitterScheduleRepository.findBySitterIdAndId(sitterId, sitterScheduleId)
                .orElseThrow(() -> new NoSuchElementException("해당 돌봄 예약이 존재하지 않습니다."));

        verifyingPermissionsSitter(memberRepository.findById(sitterId)
                .orElseThrow(() -> new NoSuchElementException("회원의 정보 조회에 실패했습니다.")));

        return sitterSchedule.toResponse();
    }

    @Comment("특정 돌봄사의 특정 돌봄 예약 취소")
    @Transactional
    public void delete(long sitterId, long sitterScheduleId) {
        Member sitter = memberRepository.findById(sitterId)
                .orElseThrow(() -> new NoSuchElementException("회원의 정보를 조회에 실패했습니다."));

        authorizationMember(sitter);
        verifyingPermissionsSitter(sitter);

        SitterSchedule sitterSchedule = sitterScheduleRepository.findBySitterIdAndId(sitterId, sitterScheduleId)
                .orElseThrow(() -> new NoSuchElementException("해당 돌봄 예약이 존재하지 않습니다."));

        CareAvailableDate careAvailableDate = careAvailableDateRepository.findBySitterIdAndAvailableAt(sitter.getId(), sitterSchedule.getReservationAt())
                .orElseThrow(() -> new NoSuchElementException("예약 취소 오류: 해당 예약 날짜를 찾을 수 없습니다."));

        sitterSchedule.cancel();
        careAvailableDate.cancel();
        sitterSchedule.getCustomerReservation().cancel();
    }

    private static void authorizationMember(Member member) {
//        String userName = SecurityContextHolder.getContext().getAuthentication().getName(); // 로그인에 사용된 아이디 값 반환
//
//        if(!member.getLoginId().equals(userName)) {
//            throw new IllegalArgumentException("회원 본인만 가능합니다.");
//        }
    }

    public static void verifyingPermissionsSitter(Member sitter) {
        if (!sitter.getRole().equals(Role.PET_SITTER)) {
            throw new IllegalArgumentException("돌봄 예약 배정 조회 및 취소는 돌봄사만 가능합니다.");
        }
    }

}
