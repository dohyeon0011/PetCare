package com.PetSitter.repository.Review;

import com.PetSitter.domain.Reservation.CustomerReservation.CustomerReservation;
import com.PetSitter.domain.Review.Review;
import com.PetSitter.dto.Reservation.ReservationSitterResponse;
import com.PetSitter.dto.Review.response.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {

    Optional<Review> findByCustomerReservation(CustomerReservation customerReservation);

    // 고객 시점 예약 엔티티의 customerId(고객)를 기준으로 모든 리뷰 조회(+페이징)
    @Query("select new com.PetSitter.dto.Review.response.ReviewResponse$GetList(r.id, cr.id, c.nickName, s.name, r.rating, r.createdAt, c.role) " +
            "from Review r " +
            "join r.customerReservation cr " +
            "join cr.customer c " +
            "join cr.sitter s " +
            "where c.id = :customerId " +
            "and r.isDeleted = false " +
            "order by r.id desc")
    Page<ReviewResponse.GetList> findByCustomerReservationCustomerId(@Param("customerId") long customerId, Pageable pageable);

    // 고객 시점 예약 엔티티의 sitterId(돌봄사)를 기준으로 모든 리뷰 조회(+페이징)
    @Query("select new com.PetSitter.dto.Review.response.ReviewResponse$GetList(r.id, cr.id, c.nickName, s.name, r.rating, r.createdAt, s.role) " +
            "from Review r " +
            "join r.customerReservation cr " +
            "join cr.customer c " +
            "join cr.sitter s " +
            "where s.id = :sitterId " +
            "and r.isDeleted = false " +
            "order by r.id desc")
    Page<ReviewResponse.GetList> findByCustomerReservationSitterId(@Param("sitterId") long sitterId, Pageable pageable);

    @Query("select new com.PetSitter.dto.Review.response.ReviewResponse$GetDetail(" +
            "r.id, cr.id, c.nickName, s.name, cr.reservationAt, r.rating, r.comment, r.createdAt) " +
            "from Review r " +
            "join r.customerReservation cr " +
            "join cr.customer c " +
            "join cr.sitter s " +
            "where r.id = :id " +
            "and r.isDeleted = false")
    Optional<ReviewResponse.GetDetail> findReviewDetail(@Param("id") long id);

    // 고객 시점 예약 엔티티의 customerId를 기준으로 특정 리뷰 조회
    Optional<Review> findByCustomerReservationCustomerIdAndIdAndIsDeletedFalse(long customerId, long reviewId);

    // 특정 돌봄사의 평균 리뷰 점수와 총 리뷰 개수 조회
    @Query("""
            select
                new com.PetSitter.dto.Reservation.ReservationSitterResponse$avgRatingAndTotalReviewsDTO(
                    avg(r.rating), count(r)
                )
                from Review r
                where r.customerReservation.sitter.id = :sitterId
                and r.isDeleted = false
                group by r.customerReservation.sitter.id
            """)
    Optional<ReservationSitterResponse.avgRatingAndTotalReviewsDTO> findAvgRatingAndTotalReviewsBySitterId(@Param("sitterId") long sitterId);
}
