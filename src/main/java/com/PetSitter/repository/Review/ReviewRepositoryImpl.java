package com.PetSitter.repository.Review;

import com.PetSitter.domain.Review.ReviewSearch;
import com.PetSitter.domain.Review.Review;
import com.PetSitter.dto.Review.response.ReviewResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

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
    public List<ReviewResponse.GetDetail> findLatestReviews() {
        return em.createQuery(
                        "select new com.PetSitter.dto.Review.response.ReviewResponse$GetDetail(" +
                                "r.id, cr.id, c.nickName, s.name, cr.reservationAt, r.rating, r.comment, r.createdAt) " +
                                "from Review r " +
                                "join r.customerReservation cr " +
                                "join cr.customer c " +
                                "join cr.sitter s " +
                                "order by r.id desc", ReviewResponse.GetDetail.class)
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

    // 이용 후기 페이지에서 전체 조회
    @Override
    public List<ReviewResponse.GetDetail> findAllReviews(int page) {
        return em.createQuery(
                        "select new com.PetSitter.dto.Review.response.ReviewResponse$GetDetail(" +
                                "r.id, cr.id, c.nickName, s.name, cr.reservationAt, r.rating, r.comment, r.createdAt) " +
                                "from Review r " +
                                "join r.customerReservation cr " +
                                "join cr.customer c " +
                                "join cr.sitter s " +
                                "order by r.id desc", ReviewResponse.GetDetail.class)
                .setFirstResult(page * 15)
                .setMaxResults(15)
                .getResultList();
    }

    // 이용 후기 페이지에서 사용자가 선택한 돌봄사의 리뷰만 전체 조회(V1: 검색 조건 @RequestParam 으로 받기)
    @Override
    public List<ReviewResponse.GetDetail> findAllReviewsBySitterV1(String sitterName, int page) {
        return em.createQuery(
                        "select new com.PetSitter.dto.Review.response.ReviewResponse$GetDetail(" +
                                "r.id, cr.id, c.nickName, s.name, cr.reservationAt, r.rating, r.comment, r.createdAt) " +
                                "from Review r " +
                                "join r.customerReservation cr " +
                                "join cr.customer c " +
                                "join cr.sitter s " +
                                "where cr.sitter.name = :sitterName " +
                                "order by r.id desc", ReviewResponse.GetDetail.class)
                .setParameter("sitterName", sitterName)
                .setFirstResult(page * 15)
                .setMaxResults(15)
                .getResultList();
    }

    // 이용 후기 페이지에서 사용자가 선택한 돌봄사의 리뷰만 전체 조회(V2: 검색 조건을 검색 전용 클래스 객체로 받기)
    @Override
    public List<ReviewResponse.GetDetail> findAllReviewsBySitterV2(ReviewSearch reviewSearch, int page) {
        /*return em.createQuery(
                        "select new com.PetSitter.dto.Review.response.ReviewResponse$GetDetail(" +
                                "r.id, cr.id, c.nickName, s.name, cr.reservationAt, r.rating, r.comment, r.createdAt) " +
                                "from Review r " +
                                "join r.customerReservation cr " +
                                "join cr.customer c " +
                                "join cr.sitter s " +
                                "where cr.sitter.name = :sitterName " +
                                "order by r.id desc", ReviewResponse.GetDetail.class)
                .setParameter("sitterName", reviewSearch.getSitter())
                .setFirstResult(page * 15)
                .setMaxResults(15)
                .getResultList();*/

        // 동적 쿼리문 jpql로 작성
        String jpql = "select new com.PetSitter.dto.Review.response.ReviewResponse$GetDetail(" +
                "r.id, cr.id, c.nickName, s.name, cr.reservationAt, r.rating, r.comment, r.createdAt) " +
                "from Review r " +
                "join r.customerReservation cr " +
                "join cr.customer c " +
                "join cr.sitter s ";

        // reviewSearch에 값이 있으면(검색 조건)
        if (reviewSearch.getSitter() != null && StringUtils.hasText(reviewSearch.getSitter())) {
            jpql += "where cr.sitter.name = :sitterName ";
        }

        jpql += "order by r.id desc";

        TypedQuery<ReviewResponse.GetDetail> query = em.createQuery(jpql, ReviewResponse.GetDetail.class)
                .setFirstResult(page * 15)
                .setMaxResults(15);

        if (reviewSearch.getSitter() != null && StringUtils.hasText(reviewSearch.getSitter())) {
            query.setParameter("sitterName", reviewSearch.getSitter());
        }

        return query.getResultList();
    }

    // 조건 검색 시 총 리뷰 개수
    @Override
    public Long countAllReviewsBySitter(ReviewSearch reviewSearch) {
        String jpql = "select count(r) from Review r " +
                "join r.customerReservation cr " +
                "join cr.customer c " +
                "join cr.sitter s ";

        if (reviewSearch.getSitter() != null && StringUtils.hasText(reviewSearch.getSitter())) {
            jpql += "where cr.sitter.name = :sitterName ";
        }

        TypedQuery<Long> query = em.createQuery(jpql, Long.class);

        if (reviewSearch.getSitter() != null && StringUtils.hasText(reviewSearch.getSitter())) {
            query.setParameter("sitterName", reviewSearch.getSitter());
        }

        return query.getSingleResult();
    }

    // 드롭다운 전체 돌봄사 목록 조회
    @Override
    public List<String> getAllSitters() {
        return em.createQuery(
                        "select s.name from Review r " +
                                "join r.customerReservation cr " +
                                "join cr.customer c " +
                                "join cr.sitter s " +
                                "order by s.name asc", String.class)
                .getResultList();
    }
}
