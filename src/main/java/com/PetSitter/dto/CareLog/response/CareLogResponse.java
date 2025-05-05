package com.PetSitter.dto.CareLog.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "돌봄 케어 로그 조회 Response DTO")
public class CareLogResponse {

    @NoArgsConstructor
    @Getter
    @Schema(description = "돌봄 케어 로그 리스트 조회 Response DTO")
    public static class GetList {
        @Schema(description = "돌봄 케어 로그 id")
        private long id;

        @Schema(description = "돌봄 예약 id")
        private long reservationId;

        @Schema(description = "케어 로그 작성 돌봄사 이름")
        private String sitterName; // 케어 로그 작성 돌봄자

        @Schema(description = "돌봄 예약 고객 닉네임")
        private String customerNickName;

        @Schema(description = "돌봄 유형")
        private String careType;

        @Schema(description = "돌봄 설명")
        private String description;

        @Schema(description = "작성일자")
        private LocalDateTime createdAt;

        public GetList(long id, long reservationId, String sitterName, String customerNickName, String careType, String description, LocalDateTime createdAt) {
            this.id = id;
            this.reservationId = reservationId;
            this.sitterName = sitterName;
            this.customerNickName = customerNickName;
            this.careType = careType;
            this.description = description;
            this.createdAt = createdAt;
        }
    }

    @NoArgsConstructor
    @Getter
    @Schema(description = "돌봄 케어 로그 상세 조회 Response DTO")
    public static class GetDetail {
        @Schema(description = "돌봄 케어 로그 id")
        private long id;

        @Schema(description = "케어 로그 작성 돌봄사 이름")
        private String sitterName; // 케어 로그 작성 돌봄자

        @Schema(description = "돌봄 유형")
        private String careType;

        @Schema(description = "돌봄 설명")
        private String description;

        @Schema(description = "돌봄 케어 사진")
        private String image;

        @Schema(description = "작성일자")
        private LocalDateTime createdAt;

        /*public GetDetail(CareLog careLog) {
            this.id = careLog.getId();
            this.sitterName = careLog.getSitterSchedule().getSitter().getName();
            this.careType = careLog.getCareType();
            this.description = careLog.getDescription();
            this.imgPath = careLog.getImgPath();
            this.createdAt = careLog.getCreatedAt();
        }*/

        public GetDetail(long id, String sitterName, String careType, String description, String image, LocalDateTime createdAt) {
            this.id = id;
            this.sitterName = sitterName;
            this.careType = careType;
            this.description = description;
            this.image = image;
            this.createdAt = createdAt;
        }
    }

    @NoArgsConstructor
    @Getter
    @Schema(description = "돌봄 케어 로그 작성 폼 데이터 Response DTO")
    public static class GetNewCareLog {

        @Schema(description = "케어 로그 작성 돌봄사 이름")
        private String sitterName;

        @Schema(description = "돌봄 예약 고객 닉네임")
        private String customerNickName;

        @Schema(description = "돌봄 유형")
        private String careType;

        @Schema(description = "돌봄 설명")
        private String description;
        
        private String imgPath;

        /*public GetNewCareLog(SitterSchedule sitterSchedule) {
            this.sitterName = sitterSchedule.getSitter().getName();
            this.customerNickName = sitterSchedule.getCustomer().getNickName();
        }*/

        public GetNewCareLog(String sitterName, String customerNickName) {
            this.sitterName = sitterName;
            this.customerNickName = customerNickName;
        }
    }
}
