package com.PetSitter.service.Reservation.SitterSchedule;

import com.PetSitter.config.annotation.ReadOnlyTransactional;
import com.PetSitter.domain.CareAvailableDate.CareAvailableDate;
import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.Role;
import com.PetSitter.domain.PointHistory.PointsHistory;
import com.PetSitter.domain.PointHistory.PointsStatus;
import com.PetSitter.domain.Reservation.SitterSchedule.SitterSchedule;
import com.PetSitter.dto.Reservation.SitterSchedule.response.SitterScheduleResponse;
import com.PetSitter.repository.CareAvailableDate.CareAvailableDateRepository;
import com.PetSitter.repository.Member.MemberRepository;
import com.PetSitter.repository.PointHistory.PointHistoryRepository;
import com.PetSitter.repository.Reservation.SitterSchedule.SitterScheduleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SitterScheduleService {

    private final MemberRepository memberRepository;
    private final SitterScheduleRepository sitterScheduleRepository;
    private final CareAvailableDateRepository careAvailableDateRepository;
    private final PointHistoryRepository pointHistoryRepository;

    /**
     * 돌봄사: 특정 돌봄사의 전체 돌봄 예약 목록 조회
     */
    @ReadOnlyTransactional
    public Page<SitterScheduleResponse.GetList> findAllById(long sitterId, Pageable pageable) {
        Member sitter = memberRepository.findById(sitterId)
                .orElseThrow(() -> new NoSuchElementException("돌봄 예약 조회 오류: 돌봄사 정보 조회에 실패했습니다."));
        verifyingPermissionsSitter(sitter);

        return sitterScheduleRepository.findBySitterId(sitterId, pageable);
    }

    /**
     * 돌봄사: 특정 돌봄사의 특정 돌봄 예약 정보 조회
     */
    @Transactional(readOnly = true)
    public SitterScheduleResponse.GetDetail findById(long sitterId, long sitterScheduleId) {
        SitterSchedule sitterSchedule = sitterScheduleRepository.findBySitterIdAndIdWithCustomerReservation(sitterId, sitterScheduleId)
                .orElseThrow(() -> new NoSuchElementException("해당 돌봄 예약이 존재하지 않습니다."));

        verifyingPermissionsSitter(memberRepository.findById(sitterId)
                .orElseThrow(() -> new NoSuchElementException("회원의 정보 조회에 실패했습니다.")));

        CareAvailableDate careAvailableDate = careAvailableDateRepository.findBySitterIdAndAvailableAt(sitterId, sitterSchedule.getReservationAt())
                .orElseThrow(() -> new EntityNotFoundException("해당 돌봄 예약 날짜가 존재하지 않습니다. (" + sitterSchedule.getReservationAt() + ")"));

        Optional<PointsHistory> usingPointHistory = pointHistoryRepository.findByCustomerReservationAndPointsStatus(sitterSchedule.getCustomerReservation(), PointsStatus.USING);

        Integer usingPoint = null;
        if (usingPointHistory.isPresent()) {
            usingPoint = usingPointHistory.get().getPoint();
        }
        return sitterSchedule.toResponse(usingPoint, careAvailableDate.getPrice());
    }

    /**
     * 특정 돌봄사의 특정 돌봄 예약 취소
     */
    @Transactional
    public void delete(long sitterId, long sitterScheduleId) {
        SitterSchedule sitterSchedule = sitterScheduleRepository.findBySitterIdAndIdWithCustomerReservation(sitterId, sitterScheduleId)
                .orElseThrow(() -> new NoSuchElementException("해당 돌봄 예약이 존재하지 않습니다."));

        // 순환 대기로 데드락을 방지하기 위해 (고객 시점)예약 취소와 동일하게 고객 -> 돌봄사 순으로 락 획득 조회.
        Member customer = memberRepository.findByIdAndFalseWithLock(sitterSchedule.getCustomer().getId())
                .orElseThrow(() -> new NoSuchElementException("로그인한 회원 정보를 불러오는데 실패했습니다."));
        Member sitter = memberRepository.findByIdAndFalseWithLock(sitterId)
                .orElseThrow(() -> new NoSuchElementException("회원의 정보를 조회에 실패했습니다."));
        authorizationMember(sitter);
        verifyingPermissionsSitter(sitter);

        CareAvailableDate careAvailableDate = careAvailableDateRepository.findBySitterIdAndAvailableAtWithLock(sitter.getId(), sitterSchedule.getReservationAt())
                .orElseThrow(() -> new NoSuchElementException("예약 취소 오류: 해당 예약 날짜를 찾을 수 없습니다."));

        Optional<PointsHistory> usingPoints = pointHistoryRepository.findByCustomerReservationAndPointsStatus(sitterSchedule.getCustomerReservation(), PointsStatus.USING);
        Optional<PointsHistory> savingPoints = pointHistoryRepository.findByCustomerReservationAndPointsStatus(sitterSchedule.getCustomerReservation(), PointsStatus.SAVING);
        usingPoints.ifPresentOrElse(
                pointsHistory -> {  // 고객이 적립금을 사용했었을 때
                    customer.addRewardPoints(pointsHistory.getPoint()); // 사용한 적립금 반환
                    customer.subRewardPoints(savingPoints.get().getPoint()); // 적립된 적립금 회수
                },
                () -> customer.subRewardPoints(savingPoints.get().getPoint()) // 적립된 적립금 회수(해당 예약 건에 적립금 사용하지 않았을 때)
        );
        sitterScheduleCancel(sitterSchedule, careAvailableDate);
    }

    private static void authorizationMember(Member member) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName(); // 로그인에 사용된 아이디 값 반환

        if(!member.getLoginId().equals(userName)) {
            throw new IllegalArgumentException("회원 본인만 가능합니다.");
        }
    }

    public static void verifyingPermissionsSitter(Member sitter) {
        if (!sitter.getRole().equals(Role.PET_SITTER)) {
            throw new IllegalArgumentException("돌봄 예약 배정 조회 및 취소는 돌봄사만 가능합니다.");
        }
    }

    @Transactional
    private static void sitterScheduleCancel(SitterSchedule sitterSchedule, CareAvailableDate careAvailableDate) {
        sitterSchedule.cancel();
        careAvailableDate.cancel();
        sitterSchedule.getCustomerReservation().cancel();
    }
}
