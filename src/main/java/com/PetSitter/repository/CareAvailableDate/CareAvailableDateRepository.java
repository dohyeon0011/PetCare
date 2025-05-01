package com.PetSitter.repository.CareAvailableDate;

import com.PetSitter.domain.CareAvailableDate.CareAvailableDate;
import com.PetSitter.dto.CareAvailableDate.response.CareAvailableDateResponse;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CareAvailableDateRepository extends JpaRepository<CareAvailableDate, Long>, CareAvailableDateRepositoryCustom, QuerydslPredicateExecutor<CareAvailableDate> {

    // 특정 회원의 돌봄 가능한 날짜 모두 조회
    List<CareAvailableDate> findBySitterId(long sitterId);

    // 특정 회원의 특정 돌봄 가능한 날짜 엔티티로 조회
    @Lock(LockModeType.PESSIMISTIC_WRITE)   // 다른 트랜잭션에서 읽기/쓰기 락(단순 select for update 락 없이 조회라면 DB와 격리단계에 따라 읽기까지는 가능할 수도, select for update 락이 서로 걸려 있는 상태라면 읽기/쓰기 락)
    Optional<CareAvailableDate> findBySitterIdAndId(long sitterId, long id);

    // 특정 회원의 특정 돌봄 가능한 날짜 DTO로 직접 조회
    @Query("select new com.PetSitter.dto.CareAvailableDate.response.CareAvailableDateResponse$GetDetail(" +
            "c.id, c.availableAt, c.price, s.zipcode, s.address, c.status) " +
            "from CareAvailableDate c " +
            "join c.sitter s " +
            "where s.id = :sitterId AND c.id = :id")
    Optional<CareAvailableDateResponse.GetDetail> findBySitterIdAndIdDetail(@Param("sitterId") long sitterId, @Param("id") long id);

    // 특정 회원의 특정 날짜 조회(배타락)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from CareAvailableDate c where c.sitter.id = :sitterId and c.availableAt = :availableAt")
    Optional<CareAvailableDate> findBySitterIdAndAvailableAtWithLock(@Param("sitterId") long sitterId, @Param("availableAt") LocalDate availableAt);

    // 특정 회원의 특정 날짜 조회(일반 조회)
    Optional<CareAvailableDate> findBySitterIdAndAvailableAt(long sitterId, LocalDate availableAt);

    // 자신이 등록한 돌봄 가능 날짜 조회 페이징
    @Query("select new com.PetSitter.dto.CareAvailableDate.response.CareAvailableDateResponse$GetList(c.id, c.availableAt, c.price, c.status) " +
            "from CareAvailableDate c where c.sitter.id = :sitterId " +
            "order by c.availableAt asc")
    Page<CareAvailableDateResponse.GetList> findBySitterId(@Param("sitterId") long sitterId, Pageable pageable);

    // 검색 조건 내에 속한 기간의 돌봄 일정 조회


}
