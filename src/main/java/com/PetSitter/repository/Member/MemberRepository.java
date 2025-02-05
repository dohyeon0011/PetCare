package com.PetSitter.repository.Member;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.Role;
import com.PetSitter.domain.Pet.Pet;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom, QuerydslPredicateExecutor<Member> {

    Optional<Member> findByLoginId(String loginId);

    /*@Query("select new com.PetCare.dto.Member.response.MemberResponse$GetSitter(" +
            "s.id, s.name, s.careerYear, s.certifications")
    List<MemberResponse.GetSitter> findAllSitter();*/

    @EntityGraph(attributePaths = {"pets"})
    @Query("select m from Member m where m.id = :id and m.role = :role")
//    @Query("select m from Member m join fetch m.pets where m.id = :id and m.role = :role")
    Optional<Member> findByCustomerId(@Param("id") long id, @Param("role") Role role);

    @EntityGraph(attributePaths = {"certifications"})
    @Query("select m from Member m where m.id = :id and m.role = :role")
    Optional<Member> findBySitterId(@Param("id") long id, @Param("role") Role role);

    @Query("select m.role from Member m where m.id = :id")
    Optional<Role> findRoleById(@Param("id") long id);

    @Query("select p from Pet p where p.customer.id = :customerId")
    List<Pet> findPetsByCustomerId(@Param("customerId") long customerId);
}
