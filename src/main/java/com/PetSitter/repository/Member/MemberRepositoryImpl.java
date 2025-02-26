package com.PetSitter.repository.Member;

import com.PetSitter.domain.CareAvailableDate.CareAvailableDate;
import com.PetSitter.domain.Member.QMember;
import com.PetSitter.domain.Member.Role;
import com.PetSitter.domain.Pet.Pet;
import com.PetSitter.dto.Member.response.MemberAdminResponse;
import com.PetSitter.dto.Member.response.QMemberAdminResponse_MemberListResponse;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.PetSitter.domain.CareAvailableDate.QCareAvailableDate.careAvailableDate;
import static com.PetSitter.domain.Member.QMember.*;
import static com.PetSitter.domain.Pet.QPet.pet;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 고객이 보유한 반려견 목록 전체 조회
    @Override
    public List<Pet> findPetsByCustomerId(long customerId) {
        return queryFactory
                .selectFrom(pet)
                .where(pet.customer.id.eq(customerId))
                .fetch();

    }

    // 돌봄사가 등록한 예약 가능 날짜 전체 조회
    @Override
    public List<CareAvailableDate> findCareAvailableDatesBySitterId(long sitterId) {
        LocalDateTime now = LocalDateTime.now();

        return queryFactory
                .selectFrom(careAvailableDate)
                .where(careAvailableDate.sitter.id.eq(sitterId)
                        .and(careAvailableDate.availableAt.after(LocalDate.from(now))))
                .fetch();
    }

    // 돌봄사가 등록한 예약 가능 날짜 중 특정 날짜 조회
    @Override
    public Optional<CareAvailableDate> findCareAvailableDateBySitterIdAndAvailableAt(long sitterId, LocalDate availableDate) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(careAvailableDate)
                        .where(careAvailableDate.sitter.id.eq(sitterId)
                                .and(careAvailableDate.availableAt.eq(availableDate)))
                        .fetchOne());
    }

    // 관리자 페이지 모든 회원 목록 조회(+페이징)
    @Override
    public Page<MemberAdminResponse.MemberListResponse> findAllMember(Pageable pageable) {
        List<MemberAdminResponse.MemberListResponse> content = queryFactory
                .select(new QMemberAdminResponse_MemberListResponse(
                        member.id, member.name, member.nickName, member.email, member.role, member.createdAt, member.isDeleted))
                .from(member)
                .where(member.role.ne(Role.ADMIN))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(member.id.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(member.id)
                .from(member);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount);
    }
}
