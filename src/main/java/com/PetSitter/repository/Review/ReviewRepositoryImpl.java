package com.PetSitter.repository.Review;

import com.PetSitter.domain.Review.Review;
import com.PetSitter.dto.Review.response.ReviewResponse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final EntityManager em;

    // 고객 시점 예약 엔티티의 sitterId를 기준으로 모든 리뷰 조회(+페이징)
    @Override
    public List<Review> findBySitterId(long sitterId, int page, int size) {
        return em.createQuery(
                        "select r from Review r " +
                                "where r.customerReservation.sitter.id = :sitterId", Review.class)
                .setParameter("sitterId", sitterId)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    // 모든 리뷰 조회(+최신 6개만)
    @Override
    public List<ReviewResponse.GetDetail> findAllReview() {
        return em.createQuery(
                        "select new com.PetSitter.dto.Review.response.ReviewResponse$GetDetail(" +
                                "r.id, cr.id, c.nickName, s.name, cr.reservationAt, r.rating, r.comment, r.createdAt) " +
                                "from Review r " +
                                "join r.customerReservation cr " +
                                "join cr.customer c " +
                                "join cr.sitter s " +
                                "order by r.createdAt desc", ReviewResponse.GetDetail.class)
                .setMaxResults(6)
                .getResultList();
    }

    // 전체 리뷰 개수 조회(돌봄 가능한 돌봄사의 자세한 정보에서 리뷰 조회 시)
    @Override
    public Long countBySitterId(long sitterId) {
        return em.createQuery(
                        "select count(r) from Review r where r.customerReservation.sitter.id = :sitterId", Long.class)
                .setParameter("sitterId", sitterId)
                .getSingleResult();
    }

}
