package com.PetSitter.service.Admin.Reservation;

import com.PetSitter.domain.CareAvailableDate.CareAvailableDate;
import com.PetSitter.domain.CareLog.CareLog;
import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.Role;
import com.PetSitter.domain.PointHistory.PointsHistory;
import com.PetSitter.domain.PointHistory.PointsStatus;
import com.PetSitter.domain.Reservation.CustomerReservation.CustomerReservation;
import com.PetSitter.domain.Reservation.ReservationSearch;
import com.PetSitter.domain.Reservation.SitterSchedule.SitterSchedule;
import com.PetSitter.dto.Reservation.CustomerReservation.response.AdminReservationResponse;
import com.PetSitter.repository.CareAvailableDate.CareAvailableDateRepository;
import com.PetSitter.repository.PointHistory.PointHistoryRepository;
import com.PetSitter.repository.Reservation.CustomerReservation.CustomerReservationRepository;
import com.PetSitter.repository.Reservation.SitterSchedule.SitterScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminReservationService {

    private final CustomerReservationRepository customerReservationRepository;
    private final SitterScheduleRepository sitterScheduleRepository;
    private final CareAvailableDateRepository careAvailableDateRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Comment("관리자 페이지 모든 예약 목록 조회")
    @Transactional(readOnly = true)
    public Page<AdminReservationResponse.ReservationListResponse> findAllForAdmin(ReservationSearch reservationSearch, Member member, Pageable pageable) {
        verifyingPermissionsAdmin(member);

        return customerReservationRepository.findAllReservation(reservationSearch, pageable);
    }

    @Comment("관리자 페이지 예약 상세 정보 조회")
    @Transactional(readOnly = true)
    public AdminReservationResponse.ReservationDetailResponse findForAdmin(long id, Member member) {
        verifyingPermissionsAdmin(member);

        CustomerReservation customerReservation = customerReservationRepository.findForAdmin(id)
                .orElseThrow(() -> new NoSuchElementException("해당 예약을 찾을 수 없습니다."));

        List<CareLog> careLogs = customerReservation.getSitter().getSitterSchedules()
                .stream()
                .filter(schedule -> schedule.getCustomerReservation().getId() == customerReservation.getId()) // 예약 ID가 일치하는 스케줄만 필터링
                .flatMap(schedule -> schedule.getCareLogList().stream()) // schedule의 CareLogList를 펼쳐서 하나의 리스트로 변환(map()과 다르게 flatMap()은 중첩된 리스트를 하나의 리스트로 풀어서 반환해줌)
                .toList();

        return new AdminReservationResponse.ReservationDetailResponse(
                customerReservation.getCustomer(),
                customerReservation.getSitter(),
                customerReservation,
                customerReservation.getPetReservations(),
                careLogs,
                customerReservation.getReview()
        );
    }

    @Comment("관리자 권한 예약 취소")
    @Transactional
    public void deleteForAdmin(long id, Member member) {
        verifyingPermissionsAdmin(member);

        CustomerReservation customerReservation = customerReservationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 예약을 찾을 수 없습니다."));

        SitterSchedule sitterSchedule = sitterScheduleRepository.findByCustomerReservation(customerReservation)
                .orElseThrow(() -> new NoSuchElementException("해당 예약과 연결된 돌봄 일정이 존재하지 않습니다."));

        CareAvailableDate careAvailableDate = careAvailableDateRepository.findBySitterIdAndAvailableAt(sitterSchedule.getSitter().getId(), customerReservation.getReservationAt())
                .orElseThrow(() -> new NoSuchElementException("예약 취소 오류: 돌봄사의 해당 예약 날짜를 찾을 수 없습니다."));

        Optional<PointsHistory> usingPoints = pointHistoryRepository.findByCustomerReservationAndPointsStatus(customerReservation, PointsStatus.USING);
        Optional<PointsHistory> savingPoints = pointHistoryRepository.findByCustomerReservationAndPointsStatus(customerReservation, PointsStatus.SAVING);

        if (usingPoints.isPresent()) {
            customerReservation.getCustomer().addRewardPoints(usingPoints.get().getPoints());
            customerReservation.getCustomer().subRewardPoints(savingPoints.get().getPoints());
        } else {
            customerReservation.getCustomer().subRewardPoints(savingPoints.get().getPoints());
        }

        customerReservation.cancel();
        sitterSchedule.cancel();
        careAvailableDate.cancel();
    }

    public static void verifyingPermissionsAdmin(Member member) {
        if (!member.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException("관리자 인증이 되지 않은 사용자입니다.");
        }
    }
}

