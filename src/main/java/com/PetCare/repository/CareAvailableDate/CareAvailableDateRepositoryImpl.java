package com.PetCare.repository.CareAvailableDate;

import com.PetCare.domain.CareAvailableDate.CareAvailableDate;
import com.PetCare.domain.CareAvailableDate.CareAvailableDateStatus;
import com.PetCare.domain.Member.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.PetCare.domain.CareAvailableDate.QCareAvailableDate.careAvailableDate;

@RequiredArgsConstructor
public class CareAvailableDateRepositoryImpl implements CareAvailableDateRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 돌봄 예약 가능 날짜를 등록한 회원 중 현재 예약이 가능한 회원만 조회
    public Set<Member> findDistinctSitters() {
        return queryFactory
                .select(careAvailableDate.sitter).distinct()
                .from(careAvailableDate)
                .where(careAvailableDate.status.eq(CareAvailableDateStatus.POSSIBILITY))
                .stream()
                .collect(Collectors.toSet());
    }

    // 특정 돌봄사의 돌봄 예약 가능한 날짜만을 조회
    public List<CareAvailableDate> findBySitterIdAndPossibility(long sitterId) {
        return queryFactory
                .selectFrom(careAvailableDate)
                .where(careAvailableDate.sitter.id.eq(sitterId)
                        .and(careAvailableDate.status.eq(CareAvailableDateStatus.POSSIBILITY)))
                .fetch();
    }
}
