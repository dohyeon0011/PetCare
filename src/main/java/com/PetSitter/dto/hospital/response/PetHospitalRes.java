package com.PetSitter.dto.hospital.response;

import com.opencsv.bean.CsvBindByName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "동물병원 CSV Res DTO")
@NoArgsConstructor
@Getter
public class PetHospitalRes {

    @Schema(description = "병원 이름")
    @CsvBindByName(column = "사업장명") // csv 파일에서 해당하는 이름의 컬럼을 필드에 매핑 시켜주는 어노테이션
    private String name;

    @Schema(description = "소재지 우편번호")
    @CsvBindByName(column = "소재지우편번호")
    private String zipcode;

    @Schema(description = "소재지 전체 주소")
    @CsvBindByName(column = "소재지전체주소")
    private String address;

    @Schema(description = "도로명 우편번호")
    @CsvBindByName(column = "도로명우편번호")
    private String streetZipcode;

    @Schema(description = "도로명 전체 주소")
    @CsvBindByName(column = "도로명전체주소")
    private String streetAddress;

    @Schema(description = "소재지 전화")
    @CsvBindByName(column = "소재지전화")
    private String tel;

    @Schema(description = "위도")
    @CsvBindByName(column = "좌표정보y(epsg5174)")
    private Double lat;

    @Schema(description = "경도")
    @CsvBindByName(column = "좌표정보x(epsg5174)")
    private Double lng;
}
