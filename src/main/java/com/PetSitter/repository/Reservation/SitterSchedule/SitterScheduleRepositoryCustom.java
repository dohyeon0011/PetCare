package com.PetSitter.repository.Reservation.SitterSchedule;

import com.PetSitter.dto.Reservation.SitterSchedule.response.SitterScheduleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SitterScheduleRepositoryCustom {

    // 돌봄사에게 배정됐던 모든 예약 조회(+페이징)
    Page<SitterScheduleResponse.GetList> findBySitterId(long sitterId, Pageable pageable);

    // 특정 돌봄사의 특정 돌봄 예약 정보 조회(+DTO로 직접 조회)
//    SitterScheduleResponse.GetDetail findSitterScheduleDetail(long sitterId, long sitterScheduleId);
}
