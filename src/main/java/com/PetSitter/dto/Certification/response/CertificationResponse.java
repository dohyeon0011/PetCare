package com.PetSitter.dto.Certification.response;

import com.PetSitter.domain.Certification.Certification;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Schema(description = "자격증 조회 Response DTO")
public class CertificationResponse {

    @NoArgsConstructor
    @Getter
    @Schema(description = "자격증 조회 리스트 Response DTO")
    public static class GetList {
        @Schema(description = "자격증 id")
        private long id;

        @Schema(description = "자격증 이름")
        private String name;

        public GetList(Certification certification) {
            this.id = certification.getId();
            this.name = certification.getName();
        }
    }

    @NoArgsConstructor
    @Getter
    @Schema(description = "예약 전 고객이 조회할 돌봄사 자격증 정보 Response DTO")
    public static class GetReservation {
        @Schema(description = "자격증 이름")
        private String name;

        public GetReservation(Certification certification) {
            this.name = certification.getName();
        }
    }
}
