package com.PetSitter.repository.Member;

import com.PetSitter.domain.CareAvailableDate.CareAvailableDate;
import com.PetSitter.domain.Member.MemberSearch;
import com.PetSitter.domain.Member.Role;
import com.PetSitter.domain.Pet.Pet;
import com.PetSitter.dto.Member.response.AdminMemberResponse;
import com.PetSitter.dto.Member.response.QAdminMemberResponse_MemberListResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.PetSitter.domain.CareAvailableDate.QCareAvailableDate.careAvailableDate;
import static com.PetSitter.domain.Member.QMember.*;
import static com.PetSitter.domain.Pet.QPet.pet;
import static org.apache.commons.lang3.StringUtils.isEmpty;

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
                        .setLockMode(LockModeType.PESSIMISTIC_WRITE)    // QueryDSL에서는 쿼리에 직접 락 모드를 지정해줘야 됨.
                        .fetchOne());
    }

    // 관리자 페이지 모든 회원 목록 조회(+페이징)
    @Override
    public Page<AdminMemberResponse.MemberListResponse> findAllMember(MemberSearch memberSearch, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder(); // 다른 메서드에서는 재사용 불가능. 현재 메서드 내에서만 사용 가능.

        if (StringUtils.hasText(memberSearch.getName()) && memberSearch.getName() != null) {
//            builder.and(member.name.lower().eq(memberSearch.getName().toLowerCase())); // 대소문자 구분 X(완전 일치)
            builder.and(member.name.containsIgnoreCase(memberSearch.getName())); // 부분 검색(포함 -> 이러면 대소문자도 구분 안하고, 포함된 문자도 검색됨.)
        }

        List<AdminMemberResponse.MemberListResponse> content = queryFactory
                .select(new QAdminMemberResponse_MemberListResponse(
                        member.id, member.name, member.nickName, member.email, member.role, member.createdAt, member.isDeleted))
                .from(member)
                .where(member.role.ne(Role.ADMIN),
                        builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(member.id.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(member.id)
                .from(member)
                .where(member.role.ne(Role.ADMIN),
                        builder);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount);
    }

    // BooleanBuilder() 대신 이 방법도 사용 가능. 두 방법 모두 where를 하나로 묶지 않고 and로 연결 가능.(이 방식 경우 다른 곳에서도 사용이 된다는 점이 있음)
    // StringUtils.hasText()로 해도 됨.
    // 빈환 타입을 BooleanExpression으로 해두면 and로 where 연결 가능.
    private BooleanExpression membernameEq(String name) {
        return isEmpty(name) ? null : member.name.eq(name);
    }
}
