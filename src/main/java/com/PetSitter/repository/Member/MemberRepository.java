package com.PetSitter.repository.Member;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.Role;
import com.PetSitter.domain.Pet.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // 리뷰가 가장 많은 대표 돌봄사 최상위 3명 조회(메인 페이지 - 자세히 보기 3 (어떤 분들이 활동하고 있나요?) 안내)
    @Query("""
        select m
        from Member m
        join fetch m.certifications
        where m.id in (
            select cr.sitter.id
            from CustomerReservation cr
            join cr.review r
            group by cr.sitter.id
            order by count(r.id) desc
        )
        """)
    Page<Member> findBestSitters(Pageable pageable);

}
