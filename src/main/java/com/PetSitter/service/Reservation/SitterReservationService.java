package com.PetSitter.service.Reservation;

import com.PetSitter.domain.CareAvailableDate.CareAvailableDate;
import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.Role;
import com.PetSitter.domain.Pet.Pet;
import com.PetSitter.domain.Review.Review;
import com.PetSitter.dto.Reservation.ReservationResponse;
import com.PetSitter.dto.Reservation.ReservationSitterResponse;
import com.PetSitter.repository.CareAvailableDate.CareAvailableDateRepository;
import com.PetSitter.repository.Member.MemberRepository;
import com.PetSitter.repository.Pet.PetRepository;
import com.PetSitter.repository.Review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class SitterReservationService {

    private final MemberRepository memberRepository;
    private final PetRepository petRepository;
    private final CareAvailableDateRepository careAvailableDateRepository;
    private final ReviewRepository reviewRepository;

    @Comment("고객에게 돌봄 예약 가능한 돌봄사들의 정보 조회")
    @Transactional(readOnly = true)
    public Page<ReservationSitterResponse.GetList> findReservableSitters(Pageable pageable) {
        /*Set<Member> sitters = careAvailableDateRepository.findDistinctSitters();

        return sitters.stream()
                .map(ReservationSitterResponse.GetList::new)
                .toList();*/

        /*Set<ReservationSitterResponse.GetList> sitters = careAvailableDateRepository.findDistinctSitterDetail();

        return sitters.stream().toList();*/

        return careAvailableDateRepository.findDistinctSitterDetail(pageable);
    }

    @Comment("돌봄 예약 가능 목록 중 선택한 돌봄사의 자세한 정보 조회")
    @Transactional(readOnly = true)
    public ReservationSitterResponse.GetDetail findReservableSitter(long sitterId, int page) {
        Member sitter = memberRepository.findById(sitterId)
                .orElseThrow(() -> new NoSuchElementException("해당 돌봄사 정보가 존재하지 않습니다."));

        List<CareAvailableDate> careAvailableDates = careAvailableDateRepository.findBySitterId(sitter.getId());
        if (careAvailableDates.isEmpty()) {
            throw new NoSuchElementException("해당 돌봄사는 돌봄 예약 가능한 날짜가 없습니다.");
        }

//        List<Review> reviews = reviewRepository.findByCustomerReservationSitterId(sitter.getId());
        List<Review> reviews = reviewRepository.findBySitterId(sitterId, page, 5); // 한 페이지에 항목 수 5개 고정.

        return new ReservationSitterResponse.GetDetail(sitter, reviews);
    }

    @Comment("고객이 예약할 때 보여줄 정보")
    @Transactional(readOnly = true)
    public ReservationResponse getReservationDetails(long customerId, long sitterId) {
        Member customer = memberRepository.findById(customerId)
                .orElseThrow(() -> new NoSuchElementException("현재 회원의 정보 조회에 실패했습니다."));

        verifyingPermissionsCustomer(customer);

        Member sitter = memberRepository.findById(sitterId)
                .orElseThrow(() -> new NoSuchElementException("해당 돌봄사 정보가 존재하지 않습니다."));

        verifyingPermissionsSitter(sitter);

        List<CareAvailableDate> careAvailableDates = careAvailableDateRepository.findBySitterIdAndPossibility(sitter.getId());

        if (careAvailableDates.isEmpty()) {
            throw new NoSuchElementException("해당 돌봄사는 돌봄 예약 가능한 날짜가 없습니다.");
        }
        List<Pet> pets = petRepository.findByCustomerId(customer.getId());

        return new ReservationResponse(customer, sitter, careAvailableDates, pets);
    }

    public static void verifyingPermissionsCustomer(Member customer) {
        if (!customer.getRole().equals(Role.CUSTOMER)) {
            throw new IllegalArgumentException("고객만 예약 등록,수정 및 삭제가 가능합니다.");
        }
    }

    public static void verifyingPermissionsSitter(Member sitter) {
        if (!sitter.getRole().equals(Role.PET_SITTER)) {
            throw new IllegalArgumentException("돌봄 예약 배정,수정 및 삭제는 돌봄사만 가능합니다.");
        }
    }

}
