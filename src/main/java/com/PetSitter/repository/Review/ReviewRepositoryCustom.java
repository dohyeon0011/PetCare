package com.PetSitter.repository.Review;

import com.PetSitter.domain.Review.ReviewSearch;
import com.PetSitter.domain.Review.Review;
import com.PetSitter.dto.Review.response.ReviewResponse;

import java.util.List;

public interface ReviewRepositoryCustom {

    // 고객 시점 예약 엔티티의 sitterId를 기준으로 모든 리뷰 조회(+페이징)
    List<Review> findBySitterId(long sitterId, int page, int size);

    // 모든 리뷰 조회(+최신 6개만)
    List<ReviewResponse.GetDetail> findLatestReviews();

    // 전체 리뷰 개수 조회(돌봄 가능한 돌봄사의 자세한 정보에서 리뷰 조회 시)
    Long countBySitterId(long sitterId);

    // 이용 후기 페이지에서 전체 조회
    List<ReviewResponse.GetDetail> findAllReviews(int page);

    // 이용 후기 페이지에서 사용자가 선택한 돌봄사의 리뷰만 전체 조회(V1: 검색 조건 @RequestParam 으로 받기)
    List<ReviewResponse.GetDetail> findAllReviewsBySitterV1(String sitterName, int page);

    // 이용 후기 페이지에서 사용자가 선택한 돌봄사의 리뷰만 전체 조회(V2: 검색 조건을 검색 전용 클래스 객체로 받기)
    List<ReviewResponse.GetDetail> findAllReviewsBySitterV2(ReviewSearch reviewSearch, int page);

    // 조건 검색 시 총 리뷰 개수
    Long countAllReviewsBySitter(ReviewSearch reviewSearch);

    // 드롭다운 전체 돌봄사 목록 조회
    List<String> getAllSitters();
}
