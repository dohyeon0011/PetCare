package com.PetSitter.repository.CareAvailableDate;

import com.PetSitter.domain.CareAvailableDate.CareAvailableDate;
import com.PetSitter.domain.CareAvailableDate.CareAvailableDateStatus;
import com.PetSitter.domain.Member.Member;
import com.PetSitter.dto.Reservation.QReservationSitterResponse_GetList;
import com.PetSitter.dto.Reservation.ReservationSitterResponse;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.PetSitter.domain.CareAvailableDate.QCareAvailableDate.careAvailableDate;

@RequiredArgsConstructor
public class CareAvailableDateRepositoryImpl implements CareAvailableDateRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 돌봄 예약 가능 날짜를 등록한 회원 중 현재 예약이 가능한 회원만 조회
    @Override
    public Set<Member> findDistinctSitters() {
        return queryFactory
                .select(careAvailableDate.sitter).distinct()
                .from(careAvailableDate)
                .where(careAvailableDate.status.eq(CareAvailableDateStatus.POSSIBILITY))
                .stream()
                .collect(Collectors.toSet());
    }

    // 돌봄 예약 가능 날짜를 등록한 회원 중 현재 예약이 가능한 회원만 조회(+DTO로 직접 조회)
    /*@Override
    public Set<ReservationSitterResponse.GetList> findDistinctSitterDetail() {
        return queryFactory
                .select(new QReservationSitterResponse_GetList(careAvailableDate.sitter.id, careAvailableDate.sitter.name, careAvailableDate.sitter.introduction)).distinct()
                .from(careAvailableDate)
                .where(careAvailableDate.status.eq(CareAvailableDateStatus.POSSIBILITY))
                .stream()
                .collect(Collectors.toSet());
    }*/

    // 돌봄 예약 가능 날짜를 등록한 회원 중 현재 예약이 가능한 회원만 조회(+DTO로 직접 조회, 페이징)
    @Override
    public Page<ReservationSitterResponse.GetList> findDistinctSitterDetail(Pageable pageable) {
        List<ReservationSitterResponse.GetList> content = queryFactory
                .select(new QReservationSitterResponse_GetList(careAvailableDate.sitter.id, careAvailableDate.sitter.name, careAvailableDate.sitter.introduction, careAvailableDate.sitter.careerYear)).distinct()
                .from(careAvailableDate)
                .where(careAvailableDate.status.eq(CareAvailableDateStatus.POSSIBILITY))
                .distinct()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(careAvailableDate.sitter.id.countDistinct()) // careAvailableDate.sitter.countDistinct()으로 해도 됨.
                .from(careAvailableDate)
                .where(careAvailableDate.status.eq(CareAvailableDateStatus.POSSIBILITY));

//        System.out.println("countQuery.fetchCount() = " + countQuery.fetchCount()); countQuery는 중복 제거를 못 함. 그래서 fetchOne()으로 총 컨텐트 개수 구해야 함.

        // fetchOne()으로 가져와야 countQuery에서 중복 제거를 해줘도 되질 않아서 여기서 중복 제거가 됨.
        /*long totalCount = countQuery.fetchOne();
        System.out.println("totalCount = " + totalCount);*/

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    // 특정 돌봄사의 돌봄 예약 가능한 날짜만을 조회
    @Override
    public List<CareAvailableDate> findBySitterIdAndPossibility(long sitterId) {
        return queryFactory
                .selectFrom(careAvailableDate)
                .where(careAvailableDate.sitter.id.eq(sitterId)
                        .and(careAvailableDate.status.eq(CareAvailableDateStatus.POSSIBILITY))
                        .and(careAvailableDate.availableAt.goe(LocalDate.now())))
                .fetch();
    }
}
