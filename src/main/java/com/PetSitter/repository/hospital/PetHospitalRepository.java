package com.PetSitter.repository.hospital;

import com.PetSitter.domain.hospital.PetHospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PetHospitalRepository extends JpaRepository<PetHospital, Long> {
    // 병원 이름 + 주소 조합으로 조회
    Optional<PetHospital> findByNameAndAddress(String name, String address);

    // 모든 병원 조회(병원마다 최신화된 1건만) + 지역 키워드 조건 검색
    @Query(value = """
            select name, zipcode, address, street_zipcode, street_address, tel, lat, lng, latest_at
                from (
                    select *, row_number() over (partition by name, address order by latest_at desc) as rn
                        from pet_hospital
                        where (:sido is null or address like concat('%', :sido, '%'))
                            and (:sigungu is null or address like concat('%', :sigungu, '%'))
                ) p
                where p.rn = 1
                limit :limit offset :offset
    """, nativeQuery = true)
    List<Object[]> findPageLatestPetHospitalsByNameAndAddressUsingNativeQuery(@Param("limit") int limit, @Param("offset") int offset, @Param("sido") String sido, @Param("sigungu") String sigungu);

    // 병원 이름 + 주소별 카운트 쿼리(페이징 시 이용) + 지역 키워드 조건 검색
    @Query(value = """
            select count(*)
                from (
                    select 1
                        from pet_hospital
                        where (:sido is null or address like concat('%', :sido, '%'))
                            and (:sigungu is null or address like concat('%', :sigungu, '%'))
                        group by name, address
                ) count_table
            """, nativeQuery = true)
    int countGroupedHospitals(@Param("sido") String sido, @Param("sigungu") String sigungu);
}
