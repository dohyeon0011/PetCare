package com.PetSitter.repository.Review;

import com.PetSitter.domain.Review.Review;
import com.PetSitter.dto.Review.response.ReviewResponse;

import java.util.List;

public interface ReviewRepositoryCustom {

    // 고객 시점 예약 엔티티의 sitterId를 기준으로 모든 리뷰 조회(+페이징)
    List<Review> findBySitterId(long sitterId, int page, int size);

    // 모든 리뷰 조회(+최신 6개만)
    List<ReviewResponse.GetDetail> findAllReview();
}
