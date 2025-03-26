package com.PetSitter.repository.PointHistory;

import com.PetSitter.domain.PointHistory.PointSearch;
import com.PetSitter.dto.PointHistory.response.AdminPointHistoryResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.util.List;

@RequiredArgsConstructor
public class PointHistoryRepositoryImpl implements PointHistoryRepositoryCustom {

    private final EntityManager em;

    // 관리자 페이지 - 모든 고객의 포인트 내역 히스토리 조회(적립, 사용 + 페이징)
    @Override
    public Page<AdminPointHistoryResponse.PointListResponse> findAll(PointSearch pointSearch, Pageable pageable) {
        String jpql = "select new com.PetSitter.dto.PointHistory.response.AdminPointHistoryResponse$PointListResponse(" +
                "ph.id, c.name, ph.point, ph.createdAt, ph.pointsStatus) " +
                "from PointsHistory ph " +
                "join ph.customer c ";

        String countJpql = "select count(ph) from PointsHistory ph " +
                "join ph.customer c ";

        if (StringUtils.hasText(pointSearch.getName())) {
            jpql += "where c.name = :name ";
            countJpql += "where c.name = :name";
        }
        jpql += "order by ph.id desc";

        TypedQuery<AdminPointHistoryResponse.PointListResponse> query = em.createQuery(jpql, AdminPointHistoryResponse.PointListResponse.class);

        if (StringUtils.hasText(pointSearch.getName())) {
            query.setParameter("name", pointSearch.getName());
        }

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<AdminPointHistoryResponse.PointListResponse> result = query.getResultList();

        long totalCount;

        if (result.size() < pageable.getPageSize()) {
            totalCount = pageable.getOffset() + result.size();
        } else {
            TypedQuery<Long> countQuery = em.createQuery(countJpql, Long.class);

            if (StringUtils.hasText(pointSearch.getName())) {
                countQuery.setParameter("name", pointSearch.getName());
            }
            totalCount = countQuery.getSingleResult();
        }

        return new PageImpl<>(result, pageable, totalCount);
    }

}
