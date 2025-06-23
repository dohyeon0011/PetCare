package com.PetSitter.service.hospital;

import com.PetSitter.domain.hospital.PetHospital;
import com.PetSitter.dto.hospital.response.PetHospitalRes;
import com.PetSitter.repository.hospital.PetHospitalRepository;
import com.PetSitter.util.coordinate.CoordinateConverter;
import com.PetSitter.util.coordinate.LatLng;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PetHospitalService {

    private final PetHospitalRepository petHospitalRepository;

    /**
     * 전국 동물병원 Save csv file
     */
    @Transactional
    public void saveFromCsv(Path csvPath) throws IOException {
        // try-with-resources -> try 문 종료 후, 선언부에 생성된 객체 알아서 닫아줌.
        try (Reader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8)) {    // CSV 파일을 UTF-8로 읽는 Reader 객체 생성
            // CSV의 헤더 이름을 DTO 필드와 매핑(DTO 클래스의 @CsvBindByName(column = "")이 붙은 필드에 매핑)
            HeaderColumnNameMappingStrategy<PetHospitalRes> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(PetHospitalRes.class);

            // CsvToBeanBuilder를 사용하면 CSV 파일을 한 번에 List<객체>로 파싱 가능(CSV 한 줄 == 객체 한 개)
            List<PetHospitalRes> petHospitals = new CsvToBeanBuilder<PetHospitalRes>(reader)
                    .withMappingStrategy(strategy)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();

            List<PetHospital> savePetHospital = new ArrayList<>();
            for (PetHospitalRes row : petHospitals) {
                // 좌표 변환: EPSG:5174 -> WGS84(지도에 마커 표시를 위해)
                LatLng wgs84 = CoordinateConverter.convertTMToWGS84(row.getLng(), row.getLat());

                // 이미 정보가 있는 병원인 경우, 정보가 업데이트 됐을 수도 있으니 전화번호, 위경도 수정
                Optional<PetHospital> existingPetHospital = petHospitalRepository.findByNameAndAddress(row.getName(), row.getAddress());
                if (existingPetHospital.isPresent()) {
                    PetHospital petHospital = existingPetHospital.get();
                    petHospital.update(row.getTel(), wgs84.lat(), wgs84.lng());
                    continue;
                }
                // 새로운 병원일 때
                PetHospital petHospital = PetHospital.builder()
                        .name(row.getName())
                        .zipcode(row.getZipcode())
                        .address(row.getAddress())
                        .streetZipcode(row.getStreetZipcode())
                        .streetAddress(row.getStreetAddress())
                        .tel(row.getTel())
                        .lat(wgs84.lat())
                        .lng(wgs84.lng())
                        .build();
                savePetHospital.add(petHospital);
            }
            petHospitalRepository.saveAll(savePetHospital);
        }
    }
}
