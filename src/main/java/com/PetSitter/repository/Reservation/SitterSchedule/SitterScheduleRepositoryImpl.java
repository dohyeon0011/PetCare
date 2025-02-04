package com.PetSitter.repository.Reservation.SitterSchedule;

import com.PetSitter.domain.Reservation.SitterSchedule.SitterSchedule;
import com.PetSitter.dto.Reservation.SitterSchedule.response.QSitterScheduleResponse_GetList;
import com.PetSitter.dto.Reservation.SitterSchedule.response.SitterScheduleResponse;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.PetSitter.domain.Reservation.SitterSchedule.QSitterSchedule.sitterSchedule;

@RequiredArgsConstructor
public class SitterScheduleRepositoryImpl implements SitterScheduleRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 돌봄사에게 배정됐던 모든 예약 조회(+페이징)
    @Override
    public Page<SitterScheduleResponse.GetList> findBySitterId(long sitterId, Pageable pageable) {
        // 컨텐트(데이터)만 가져오기
        List<SitterScheduleResponse.GetList> content = queryFactory
                .select(new QSitterScheduleResponse_GetList(
                        sitterSchedule.id, sitterSchedule.reservationAt, sitterSchedule.createdAt, sitterSchedule.status))
                .from(sitterSchedule)
                .where(sitterSchedule.sitter.id.eq(sitterId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 조회된 데이터가 페이지 항목 수보다 많을 때만 countQuery가 나감.(fetchCount() 호출)
        JPAQuery<SitterSchedule> countQuery = queryFactory
                .select(sitterSchedule)
                .from(sitterSchedule)
                .where(sitterSchedule.sitter.id.eq(sitterId));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount);
    }

    // 특정 돌봄사의 특정 돌봄 예약 정보 조회(+DTO로 직접 조회)
    /*@Override
    public SitterScheduleResponse.GetDetail findSitterScheduleDetail(long sitterId, long sitterScheduleId) {
        return queryFactory
                .select(new QSitterScheduleResponse_GetDetail(
                        sitterSchedule.id, sitterSchedule.customer.id, sitterSchedule.customer.nickName, sitterSchedule.sitter.id, sitterSchedule.sitter.name, sitterSchedule.price, sitterSchedule.reservationAt,
                        sitterSchedule.sitter.zipcode, sitterSchedule.sitter.address, sitterSchedule.createdAt, sitterSchedule.status, sitterSchedule.petReservations,
                        sitterSchedule.customerReservation.review.id, sitterSchedule.customerReservation.id, sitterSchedule.customerReservation.review.rating, sitterSchedule.customerReservation.review.comment
                )).distinct()
                .from(sitterSchedule)
                .join(sitterSchedule.customerReservation)
                .leftJoin(sitterSchedule.customerReservation.review)
                .join(sitterSchedule.customer)
                .join(sitterSchedule.sitter)
                .join(sitterSchedule.petReservations)
                .where(sitterSchedule.id.eq(sitterScheduleId)
                        .and(sitterSchedule.sitter.id.eq(sitterId)))
                .fetchFirst();
    }*/

}
