package com.PetSitter.repository.PointHistory;

import com.PetSitter.domain.PointHistory.PointSearch;
import com.PetSitter.dto.PointHistory.response.AdminPointHistoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PointHistoryRepositoryCustom {
    // 관리자 페이지 - 모든 고객의 포인트 내역 히스토리 조회(적립, 사용 + 페이징)
    Page<AdminPointHistoryResponse.PointListResponse> findAll(PointSearch pointSearch, Pageable pageable);
}
