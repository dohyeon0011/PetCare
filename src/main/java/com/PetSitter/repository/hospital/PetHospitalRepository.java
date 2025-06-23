package com.PetSitter.repository.hospital;

import com.PetSitter.domain.hospital.PetHospital;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PetHospitalRepository extends JpaRepository<PetHospital, Long> {
    // 병원 이름 + 주소 조합으로 조회
    Optional<PetHospital> findByNameAndAddress(String name, String address);
}
