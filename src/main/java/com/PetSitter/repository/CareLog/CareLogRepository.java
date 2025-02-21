package com.PetSitter.repository.CareLog;

import com.PetSitter.domain.CareLog.CareLog;
import com.PetSitter.dto.CareLog.response.CareLogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CareLogRepository extends JpaRepository<CareLog, Long> {

    // 돌봄사가 작성한 모든 돌봄 케어 로그 조회(엔티티로 조회 후 DTO로 변환)
    List<CareLog> findAllBySitterScheduleSitterId(long sitterId);

    // 돌봄사가 작성한 모든 돌봄 케어 로그 조회(DTO로 직접 조회)
    @Query("select new com.PetSitter.dto.CareLog.response.CareLogResponse$GetList(c.id, ss.customerReservation.id, s.name, ss.customer.nickName, c.careType, c.description, c.createdAt) " +
            "from CareLog c " +
            "join c.sitterSchedule ss " +
            "join ss.sitter s " +
            "where s.id = :sitterId " +
            "order by c.id desc")
    Page<CareLogResponse.GetList> findAllBySitterId(@Param("sitterId") long sitterId, Pageable pageable);

    // 돌봄사가 특정 돌봄에 대해 작성한 돌봄 케어 로그 전체 조회(엔티티로 조회 후 DTO로 변환)
    List<CareLog> findAllBySitterScheduleSitterIdAndSitterScheduleId(long sitterId, long sitterScheduleId);

    // 돌봄사가 특정 돌봄에 대해 작성한 돌봄 케어 로그 전체 조회(엔티티로 조회 후 DTO로 변환)
    @Query("select new com.PetSitter.dto.CareLog.response.CareLogResponse$GetDetail(c.id, s.name, c.careType, c.description, c.imgPath, c.createdAt) " +
            "from CareLog c " +
            "join c.sitterSchedule ss " +
            "join ss.sitter s " +
            "where s.id = :sitterId AND ss.id = :sitterScheduleId")
    List<CareLogResponse.GetDetail> findAllCareLogDetail(@Param("sitterId") long sitterId, @Param("sitterScheduleId") long sitterScheduleId);

    // 돌봄사가 특정 돌봄에 대해 작성한 특정 돌봄 케어 로그 조회(DTO로 조회)
    @Query("select new com.PetSitter.dto.CareLog.response.CareLogResponse$GetDetail(c.id, s.name, c.careType, c.description, c.imgPath, c.createdAt) " +
            "from CareLog c " +
            "join c.sitterSchedule ss " +
            "join ss.sitter s " +
            "where c.id = :careLogId AND s.id = :sitterId")
    Optional<CareLogResponse.GetDetail> findCareLogDetail(@Param("careLogId") long careLogId, @Param("sitterId") long sitterId);

    // 돌봄사가 특정 돌봄에 대해 작성한 특정 돌봄 케어 로그 조회
    Optional<CareLog> findBySitterScheduleSitterIdAndId(long sitterId, long careLogId);
}
