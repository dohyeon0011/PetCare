package com.PetSitter.dto.CareLog.request;

import com.PetSitter.domain.CareLog.CareLog;
import com.PetSitter.domain.Reservation.SitterSchedule.SitterSchedule;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Schema(description = "돌봄 케어 로그 작성 Req DTO")
public class AddCareLogRequest {

//    @NotBlank(message = "케어 로그를 작성할 돌봄을 선택해야 합니다.")
//    private long sitterScheduleId;

    @Schema(description = "돌봄 유형", pattern = "ex) 산책, 간식, 낮잠, 훈련")
    private String careType;

    @Schema(description = "돌봄 내용 상세 설명")
    @NotBlank(message = "해당 돌봄 케어에 대한 상세 설명을 필수로 적어야 합니다.")
    private String description;

    public CareLog toEntity(SitterSchedule sitterSchedule, String image) {
        return CareLog.builder()
                .sitterSchedule(sitterSchedule)
                .careType(careType)
                .description(description)
                .image(image)
                .build();
    }
}
