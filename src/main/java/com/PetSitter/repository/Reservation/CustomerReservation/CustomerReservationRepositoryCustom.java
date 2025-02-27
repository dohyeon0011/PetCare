package com.PetSitter.repository.Reservation.CustomerReservation;

import com.PetSitter.domain.Reservation.ReservationSearch;
import com.PetSitter.dto.Reservation.CustomerReservation.response.AdminReservationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerReservationRepositoryCustom {

    // 관리자 페이지 모든 예약 목록 조회
    Page<AdminReservationResponse.ReservationListResponse> findAllReservation(ReservationSearch reservationSearch, Pageable pageable);
}
