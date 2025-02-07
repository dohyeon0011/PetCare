package com.PetSitter.dto.CareAvailableDate.request;

import com.PetSitter.domain.CareAvailableDate.CareAvailableDateStatus;
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
public class UpdateCareAvailableDateRequest {

    @NotNull(message = "날짜 입력은 필수입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate availableAt;

    @NotNull
    @Min(value = 1, message = "돌봄 비용 입력은 필수입니다.")
    private int price;

    @NotNull(message = "상태 입력은 필수입니다.")
    private CareAvailableDateStatus status;
}
