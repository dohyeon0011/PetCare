package com.PetSitter.repository.Reservation.CustomerReservation;

import com.PetSitter.domain.Reservation.CustomerReservation.CustomerReservation;
import com.PetSitter.domain.Reservation.SitterSchedule.SitterSchedule;
import com.PetSitter.dto.Reservation.CustomerReservation.response.CustomerReservationResponse;
import com.PetSitter.dto.Review.response.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerReservationRepository extends JpaRepository<CustomerReservation, Long>, CustomerReservationRepositoryCustom {

    // 특정 고객의 돌봄 예약 내역 전체 조회
    List<CustomerReservation> findByCustomerId(long customerId);

    // 고객의 번호로 특정 회원의 특정 돌봄 예약 내역 조회(+엔티티로 직접 조회 후 DTO로 변환)
    Optional<CustomerReservation> findByCustomerIdAndId(long customerId, long id);

    // 고객의 번호로 특정 회원의 특정 돌봄 예약 내역 조회(+DTO로 직접 조회)
    @Query("select new com.PetSitter.dto.Review.response.ReviewResponse$GetNewReview(" +
            "cr.id, c.nickName, s.name) " +
            "from CustomerReservation cr " +
            "join cr.customer c " +
            "join cr.sitter s " +
            "where cr.id = :id and c.id = :customerId")
    Optional<ReviewResponse.GetNewReview> findReviewResponseDetail(@Param("customerId") long customerId, @Param("id") long id);

    // 특정 고객의 돌봄 예약 내역 전체 조회(+페이징)
    @Query("select new com.PetSitter.dto.Reservation.CustomerReservation.response.CustomerReservationResponse$GetList(" +
            "cr.id, s.name, cr.reservationAt, cr.createdAt, cr.status) " +
            "from CustomerReservation cr " +
            "join cr.sitter s " +
            "where cr.customer.id = :customerId " +
            "order by cr.id desc")
    Page<CustomerReservationResponse.GetList> findByCustomerId(@Param("customerId") long customerId, Pageable pageable);

    // 돌봄사의 예약 번호로 특정 회원의 특정 돌봄 예약 내역 조회
//    Optional<CustomerReservation> findBySitterScheduleId(long sitterScheduleId);

    // 관리자 페이지 예약 상세 정보 조회
    @Query("select cr " +
            "from CustomerReservation cr " +
            "join fetch cr.customer c " +
            "join fetch cr.sitter s " +
            "join fetch cr.petReservations p " +
            "left join fetch cr.review r " +
            "where cr.id = :id")
    Optional<CustomerReservation> findForAdmin(@Param("id") long id);
}
