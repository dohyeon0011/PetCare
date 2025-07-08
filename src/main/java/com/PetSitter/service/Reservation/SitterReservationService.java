package com.PetSitter.service.Reservation;

import com.PetSitter.config.annotation.ReadOnlyTransactional;
import com.PetSitter.domain.CareAvailableDate.CareAvailableDate;
import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.Role;
import com.PetSitter.domain.Pet.Pet;
import com.PetSitter.domain.Review.Review;
import com.PetSitter.dto.Reservation.ReservationResponse;
import com.PetSitter.dto.Reservation.ReservationSitterResponse;
import com.PetSitter.dto.Review.response.ReviewResponse;
import com.PetSitter.repository.CareAvailableDate.CareAvailableDateRepository;
import com.PetSitter.repository.Member.MemberRepository;
import com.PetSitter.repository.Pet.PetRepository;
import com.PetSitter.repository.Review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * 예약 전 고객이 보게 될 돌봄사 정보
 */
@Service
@RequiredArgsConstructor
public class SitterReservationService {

    private final MemberRepository memberRepository;
    private final PetRepository petRepository;
    private final CareAvailableDateRepository careAvailableDateRepository;
    private final ReviewRepository reviewRepository;

    /**
     * 고객에게 돌봄 예약 가능한 돌봄사들의 정보 조회
     * @ReadOnlyTransactional: 커스텀 읽기 전용 어노테이션
     */
    @ReadOnlyTransactional
    public Page<ReservationSitterResponse.GetList> findReservableSitters(Pageable pageable) {
        return careAvailableDateRepository.findDistinctSitterDetail(pageable);
    }

    /**
     * 돌봄 예약 가능 목록 중 선택한 돌봄사의 자세한 정보 조회
     */
    @Transactional(readOnly = true)
    public ReservationSitterResponse.GetDetail findReservableSitter(long sitterId, int page) {
        Member sitter = memberRepository.findById(sitterId)
                .orElseThrow(() -> new NoSuchElementException("해당 돌봄사 정보가 존재하지 않습니다."));

        List<Review> reviews = reviewRepository.findBySitterId(sitterId, page, 5); // 한 페이지에 항목 수 5개 고정.
        ReservationSitterResponse.avgRatingAndTotalReviewsDTO avgRatingAndTotalReviewsDTO = reviewRepository.findAvgRatingAndTotalReviewsBySitterId(sitterId)
                .map(result -> new ReservationSitterResponse.avgRatingAndTotalReviewsDTO(result.getAvgRating(), result.getTotalReviewCnt()))
                .orElseGet(() -> new ReservationSitterResponse.avgRatingAndTotalReviewsDTO(null, 0L));

        return new ReservationSitterResponse.GetDetail(sitter, reviews, avgRatingAndTotalReviewsDTO.getAvgRating(), avgRatingAndTotalReviewsDTO.getTotalReviewCnt());
    }

    /**
     * 고객이 예약할 때 보여줄 정보
     * @ReadOnlyTransactional: 커스텀 읽기 전용 어노테이션
     */
    @ReadOnlyTransactional
    public ReservationResponse getReservationDetails(long customerId, long sitterId) {
        Member customer = memberRepository.findById(customerId)
                .orElseThrow(() -> new NoSuchElementException("현재 회원의 정보 조회에 실패했습니다."));
        Member sitter = memberRepository.findById(sitterId)
                .orElseThrow(() -> new NoSuchElementException("해당 돌봄사 정보가 존재하지 않습니다."));
        verifyingPermissionsCustomer(customer);
        verifyingPermissionsSitter(sitter);

        List<CareAvailableDate> careAvailableDates = careAvailableDateRepository.findBySitterIdAndPossibility(sitterId);
        if (careAvailableDates.isEmpty()) {
            throw new NoSuchElementException("해당 돌봄사는 돌봄 예약 가능한 날짜가 없습니다.");
        }

        List<Pet> pets = petRepository.findByCustomerIdAndIsDeletedFalse(customerId);
        if (pets.isEmpty()) {
            throw new IllegalArgumentException("돌봄에 맡길 반려견을 마이페이지에서 등록 후 다시 예약을 진행해주세요.");
        }
        return new ReservationResponse(customer, sitter, careAvailableDates, pets);
    }

    /**
     * 선택한 돌봄사의 자세한 정보 중 리뷰 정보만 더 조회
     */
    @Transactional(readOnly = true)
    public List<ReviewResponse.GetDetail> getReviewsBySitterId(long sitterId, int page, int size) {
        List<Review> reviews = reviewRepository.findBySitterId(sitterId, page, size);
        return reviews.stream()
                .map(review -> new ReviewResponse.GetDetail(
                        review.getId(),
                        review.getCustomerReservation().getId(),
                        review.getCustomerReservation().getCustomer().getNickName(),
                        review.getCustomerReservation().getSitter().getName(),
                        review.getCustomerReservation().getReservationAt(),
                        review.getRating(),
                        review.getComment(),
                        review.getCreatedAt()))
                .toList();
    }

    /**
     * 전체 리뷰 개수 조회(돌봄 가능한 돌봄사의 자세한 정보에서 리뷰 조회 시)
     */
    @ReadOnlyTransactional
    public long getTotalReviewsBySitterId(long sitterId) {
        return reviewRepository.countBySitterId(sitterId);
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
