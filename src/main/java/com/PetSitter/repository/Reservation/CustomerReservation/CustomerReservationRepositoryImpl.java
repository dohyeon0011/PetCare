package com.PetSitter.repository.Reservation.CustomerReservation;

import com.PetSitter.domain.Reservation.ReservationSearch;
import com.PetSitter.dto.Reservation.CustomerReservation.response.AdminReservationResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.util.List;

@RequiredArgsConstructor
public class CustomerReservationRepositoryImpl implements CustomerReservationRepositoryCustom {

    private final EntityManager em;

    // 관리자 페이지 모든 예약 목록 조회
    @Override
    public Page<AdminReservationResponse.ReservationListResponse> findAllReservation(ReservationSearch reservationSearch, Pageable pageable) {
        String jpql = "select new com.PetSitter.dto.Reservation.CustomerReservation.response.AdminReservationResponse$ReservationListResponse(" +
                "cr.id, c.nickName, s.name, cr.reservationAt, cr.createdAt, cr.status) " +
                "from CustomerReservation cr " +
                "join cr.customer c " +
                "join cr.sitter s ";

        String countJpql = "select count(cr) from CustomerReservation cr " +
                "join cr.customer c " +
                "join cr.sitter s ";

        if (StringUtils.hasText(reservationSearch.getName()) && reservationSearch.getName() != null) {
            jpql += "where c.name = :name "; // 고객의 이름을 기준으로 검색하기
            countJpql += "where c.name = :name";
        }

        jpql += "order by cr.id desc";

        TypedQuery<AdminReservationResponse.ReservationListResponse> query = em.createQuery(jpql, AdminReservationResponse.ReservationListResponse.class);

        if (StringUtils.hasText(reservationSearch.getName()) && reservationSearch.getName() != null) {
            query.setParameter("name", reservationSearch.getName());
        }

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<AdminReservationResponse.ReservationListResponse> resultList = query.getResultList();

        // 결과 개수가 현재 페이지 크기보다 적으면 count 쿼리 실행 X
        long totalCount;

        if (resultList.size() < pageable.getPageSize()) {
            totalCount = pageable.getOffset() + resultList.size(); // 현재까지의 데이터 개수로 총 개수 계산
        } else {
            TypedQuery<Long> countQuery = em.createQuery(countJpql, Long.class);

            if (StringUtils.hasText(reservationSearch.getName()) && reservationSearch.getName() != null) {
                countQuery.setParameter("name", reservationSearch.getName());
            }
            totalCount = countQuery.getSingleResult(); // 전체 개수 조회
        }

        return new PageImpl<>(resultList, pageable, totalCount);
    }
}
