package com.PetSitter.dto.CareLog.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateCareLogRequest {

//    @NotNull(message = "케어 로그를 작성할 돌봄을 선택해야 합니다.")
//    private long sitterScheduleId;

    private String careType;

    @NotBlank(message = "해당 돌봄 케어에 대한 상세 설명을 필수로 적어야 합니다.")
    private String description;

    private String image;
}
