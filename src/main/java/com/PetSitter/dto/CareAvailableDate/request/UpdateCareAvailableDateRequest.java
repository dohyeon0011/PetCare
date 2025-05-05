package com.PetSitter.dto.CareAvailableDate.request;

import com.PetSitter.domain.CareAvailableDate.CareAvailableDateStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperties;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Schema(description = "돌봄 가능 날짜 수정 Req DTO")
public class UpdateCareAvailableDateRequest {

    @Schema(description = "수정할 돌봄 가능 날짜", pattern = "yyyy-MM-dd")
    @NotNull(message = "날짜 입력은 필수입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate availableAt;

    @Schema(description = "수정할 돌봄 비용")
    @NotNull
    @Min(value = 1, message = "돌봄 비용 입력은 필수입니다.")
    private int price;

    @Schema(description = "돌봄 가능 상태", pattern = "POSSIBILITY(예약 가능), IMPOSSIBILITY(예약 불가능)")
    @NotNull(message = "상태 입력은 필수입니다.")
    private CareAvailableDateStatus status;
}
