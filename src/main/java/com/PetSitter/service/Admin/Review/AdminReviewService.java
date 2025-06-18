package com.PetSitter.service.Admin.Review;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.Role;
import com.PetSitter.domain.Review.Review;
import com.PetSitter.repository.Review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AdminReviewService {

    private final ReviewRepository reviewRepository;

    /**
     * 관리자 권한 리뷰 삭제
     */
    public void deleteForAdmin(long id, Member admin) {
        verifyingPermissionsAdmin(admin);

        Review findReview = reviewRepository.findById(id) // findById()에 @Transactional(readOnly=true) 달려있음.
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 리뷰입니다."));
        findReview.changeIsDeleted(true);
        reviewRepository.saveAndFlush(findReview); // 단건 update는 @Transactional 제거 후 직접 db에 flush가 비용이 쌈. 대신, 영속성 컨텍스트 관리 비용이 들어감.(jpql or native query로도 가능.)
    }

    public static void verifyingPermissionsAdmin(Member admin) {
        if (!admin.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException("관리자 인증이 되지 않은 사용자입니다.");
        }
    }
}
