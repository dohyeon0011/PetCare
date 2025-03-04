package com.PetSitter.service.Review;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.Role;
import com.PetSitter.domain.Review.Review;
import com.PetSitter.repository.Review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AdminReviewService {

    private final ReviewRepository reviewRepository;

    @Comment("관리자 권한 리뷰 삭제")
    @Transactional
    public void deleteForAdmin(long id, Member member) {
        verifyingPermissionsAdmin(member);

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 리뷰입니다."));

        review.changeIsDeleted(true);
    }

    public static void verifyingPermissionsAdmin(Member member) {
        if (!member.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException("관리자 인증이 되지 않은 사용자입니다.");
        }
    }
}
