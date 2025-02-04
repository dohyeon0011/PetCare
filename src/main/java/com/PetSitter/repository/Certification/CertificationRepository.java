package com.PetSitter.repository.Certification;

import com.PetSitter.domain.Certification.Certification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CertificationRepository extends JpaRepository<Certification, Long> {

    // 특정 회원의 자격증 목록 조회
    List<Certification> findBySitterId(long sitterId);

    Optional<Certification> findBySitterIdAndId(long sitterId, long id);

    // 특정 회원의 특정 자격증 목록 조회
    List<Certification> findBySitterIdAndIdIn(long sitterId, List<Long> ids);
}
