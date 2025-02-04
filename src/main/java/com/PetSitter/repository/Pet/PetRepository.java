package com.PetSitter.repository.Pet;

import com.PetSitter.domain.Pet.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {

    // 특정 회원의 반려견 목록 조회
    List<Pet> findByCustomerId(long customerId);

    Optional<Pet> findByCustomerIdAndId(long customerId, long id);

    // 특정 회원이 보유중인 특정 반려견 목록만 조회
    List<Pet> findByCustomerIdAndIdIn(long customerId, List<Long> ids);
}
