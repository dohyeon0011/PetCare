package com.PetSitter.repository.PointHistory;

import com.PetSitter.domain.PointHistory.PointsHistory;
import com.PetSitter.domain.PointHistory.PointsStatus;
import com.PetSitter.domain.Reservation.CustomerReservation.CustomerReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointHistoryRepository extends JpaRepository<PointsHistory, Long>, PointHistoryRepositoryCustom {

    // 예약(고객 시점)으로 포인트 내역 조회('USING')
    Optional<PointsHistory> findByCustomerReservationAndPointsStatus(CustomerReservation customerReservation, PointsStatus pointsStatus);
}
