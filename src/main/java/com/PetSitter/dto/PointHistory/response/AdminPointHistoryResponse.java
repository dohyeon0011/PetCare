package com.PetSitter.dto.PointHistory.response;

import com.PetSitter.domain.PointHistory.PointsStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class AdminPointHistoryResponse {

    @NoArgsConstructor
    @Getter
    public static class PointListResponse {
        private long id;
        private String customerName;
        private int point;
        private LocalDateTime createdAt;
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
