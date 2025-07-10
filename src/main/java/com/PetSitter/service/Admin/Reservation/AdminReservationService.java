package com.PetSitter.service.Admin.Reservation;

import com.PetSitter.config.annotation.ReadOnlyTransactional;
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
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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

    /**
     * 관리자 페이지 모든 예약 목록 조회
     * @ReadOnlyTransactional: 커스텀 읽기 전용 어노테이션
     */
    @ReadOnlyTransactional
    public Page<AdminReservationResponse.ReservationListResponse> findAllForAdmin(ReservationSearch reservationSearch, Member admin, Pageable pageable) {
        verifyingPermissionsAdmin(admin);
        return customerReservationRepository.findAllReservation(reservationSearch, pageable);
    }

    /**
     * 관리자 페이지 발생한 총 예약 금액 조회
     */
    public Long getTotalReservationAmount(Member admin) {
        verifyingPermissionsAdmin(admin);
        return customerReservationRepository.findAllTotalReservationAmountForAdmin();
    }

    /**
     * 관리자 페이지 예약 상세 정보 조회
     */
    @Transactional(readOnly = true)
    public AdminReservationResponse.ReservationDetailResponse findForAdmin(long id, Member admin) {
        verifyingPermissionsAdmin(admin);

        CustomerReservation findCustomerReservation = customerReservationRepository.findForAdmin(id)
                .orElseThrow(() -> new NoSuchElementException("해당 예약을 찾을 수 없습니다."));

        CareAvailableDate findCareAvailableDate = careAvailableDateRepository.findBySitterIdAndAvailableAt(findCustomerReservation.getSitter().getId(), findCustomerReservation.getReservationAt())
                .orElseThrow(() -> new EntityNotFoundException("해당 돌봄 예약 날짜가 존재하지 않습니다. (" + findCustomerReservation.getReservationAt() + ")"));

        Optional<PointsHistory> findUsingPointHistory = pointHistoryRepository.findByCustomerReservationAndPointsStatus(findCustomerReservation, PointsStatus.USING);

        Integer usingPoint = null;
        if (findUsingPointHistory.isPresent()) {
            usingPoint = findUsingPointHistory.get().getPoint();
        }

        List<CareLog> findCareLogs = findCustomerReservation.getSitter().getSitterSchedules()
                .stream()
                .filter(schedule -> schedule.getCustomerReservation().getId() == findCustomerReservation.getId()) // 예약 ID가 일치하는 스케줄만 필터링
                .flatMap(schedule -> schedule.getCareLogList().stream()) // schedule의 CareLogList를 펼쳐서 하나의 리스트로 변환(map()과 다르게 flatMap()은 중첩된 리스트를 하나의 리스트로 풀어서 반환해줌)
                .toList();

        return new AdminReservationResponse.ReservationDetailResponse(
                findCustomerReservation.getCustomer(),
                findCustomerReservation.getSitter(),
                findCustomerReservation,
                findCustomerReservation.getPetReservations(),
                findCareLogs,
                findCustomerReservation.getReview(),
                usingPoint,
                findCareAvailableDate.getPrice()
        );
    }

    /**
     * 관리자 권한 예약 취소
     */
    @Transactional
    public void deleteForAdmin(long id, Member admin) {
        verifyingPermissionsAdmin(admin);

        CustomerReservation findCustomerReservation = customerReservationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 예약을 찾을 수 없습니다."));

        SitterSchedule findSitterSchedule = sitterScheduleRepository.findByCustomerReservation(findCustomerReservation)
                .orElseThrow(() -> new NoSuchElementException("해당 예약과 연결된 돌봄 일정이 존재하지 않습니다."));

        CareAvailableDate findCareAvailableDate = careAvailableDateRepository.findBySitterIdAndAvailableAtWithLock(findSitterSchedule.getSitter().getId(), findCustomerReservation.getReservationAt())
                .orElseThrow(() -> new NoSuchElementException("예약 취소 오류: 돌봄사의 해당 예약 날짜를 찾을 수 없습니다."));

        Optional<PointsHistory> existingUsingPoints = pointHistoryRepository.findByCustomerReservationAndPointsStatus(findCustomerReservation, PointsStatus.USING);
        Optional<PointsHistory> existingSavingPoints = pointHistoryRepository.findByCustomerReservationAndPointsStatus(findCustomerReservation, PointsStatus.SAVING);

        Member findCustomer = findCustomerReservation.getCustomer();

        existingUsingPoints.ifPresentOrElse(
                pointsHistory -> {  // 고객이 적립금을 사용했었을 때
                    findCustomer.addRewardPoints(pointsHistory.getPoint()); // 사용한 적립금 반환
                    findCustomer.subRewardPoints(existingSavingPoints.get().getPoint()); // 적립된 적립금 회수
                },
                () -> findCustomer.subRewardPoints(existingSavingPoints.get().getPoint()) // 적립된 적립금 회수(해당 예약 건에 적립금 사용하지 않았을 때)
        );
        cancelReservation(findCustomerReservation, findSitterSchedule, findCareAvailableDate);
    }

    public static void verifyingPermissionsAdmin(Member admin) {
        if (!admin.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException("관리자 인증이 되지 않은 사용자입니다.");
        }
    }

    private static void cancelReservation(CustomerReservation customerReservation, SitterSchedule sitterSchedule, CareAvailableDate careAvailableDate) {
        customerReservation.cancel();
        sitterSchedule.cancel();
        careAvailableDate.cancel();
    }
}

