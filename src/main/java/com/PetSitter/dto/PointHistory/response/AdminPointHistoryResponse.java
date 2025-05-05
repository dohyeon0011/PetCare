package com.PetSitter.dto.PointHistory.response;

import com.PetSitter.domain.PointHistory.PointsStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "관리자 페이지 - 회원 포인트 내역 조회 Response DTO")
public class AdminPointHistoryResponse {

    @NoArgsConstructor
    @Getter
    @Schema(description = "관리자 페이지 - 회원 포인트 내역 리스트 조회 Response DTO")
    public static class PointListResponse {
        @Schema(description = "포인트 내역 id")
        private long id;

        @Schema(description = "고객 이름")
        private String customerName;

        @Schema(description = "포인트")
        private int point;

        @Schema(description = "발생일자")
        private LocalDateTime createdAt;

        @Schema(description = "적립 or 사용 여부", pattern = "SAVING(적립), USING(사용)")
        private PointsStatus pointsStatus;

        public PointListResponse(long id, String customerName, int point, LocalDateTime createdAt, PointsStatus pointsStatus) {
            this.id = id;
            this.customerName = customerName;
            this.point = point;
            this.createdAt = createdAt;
            this.pointsStatus = pointsStatus;
        }
    }
}
