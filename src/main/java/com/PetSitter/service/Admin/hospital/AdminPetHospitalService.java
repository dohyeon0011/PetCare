package com.PetSitter.service.Admin.hospital;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.Role;
import com.PetSitter.domain.hospital.PetHospital;
import com.PetSitter.dto.hospital.response.PetHospitalDTO;
import com.PetSitter.repository.hospital.PetHospitalRepository;
import com.PetSitter.util.coordinate.CoordinateConverter;
import com.PetSitter.util.coordinate.LatLng;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminPetHospitalService {

    private final PetHospitalRepository petHospitalRepository;

    @Value("${petHospitalUpload.path}")
    private String petHospitalUploadPath;

    /**
     * 전국 동물 병원 Save csv file
     */
    @Transactional
    public void saveFromCsv(MultipartFile file, Member admin) throws IOException {
        log.info("AdminPetHospitalService - saveFromCsv(): 호출 성공.");
        verifyingPermissionsAdmin(admin);
        Path csvPath = getCsvPath(file);

        // try-with-resources -> try 문 종료 후, 선언부에 생성된 객체 알아서 닫아줌.
        try (Reader reader = Files.newBufferedReader(csvPath, StandardCharsets.ISO_8859_1)) {    // CSV 파일을 UTF-8로 읽는 Reader 객체 생성
            // CSV의 헤더 이름을 DTO 필드와 매핑(DTO 클래스의 @CsvBindByName(column = "")이 붙은 필드에 매핑)
            HeaderColumnNameMappingStrategy<PetHospitalDTO> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(PetHospitalDTO.class);

            // CsvToBeanBuilder를 사용하면 CSV 파일을 한 번에 List<객체>로 파싱 가능(CSV 한 줄 == 객체 한 개)
            List<PetHospitalDTO> petHospitalDTOList = parsingPetHospitalDTO(reader, strategy);

            List<PetHospital> savePetHospital = new ArrayList<>();
            for (PetHospitalDTO row : petHospitalDTOList) {
                if (row == null) {
                    log.warn("this row is null.");
                    continue;
                }
                log.info("row.getName()={}, row.getAddress()={}", row.getName(), row.getAddress());
                if (row.getLat() == null || row.getLng() == null) {
                    log.warn("위도/경도 value is null. lat={}, lng={}", row.getLat(), row.getLng());
                    continue;
                }
                // 좌표 변환: EPSG:5174 -> WGS84(지도에 마커 표시를 위해)
                LatLng wgs84 = CoordinateConverter.convertTMToWGS84(row.getLng(), row.getLat());

                // 이미 정보가 있는 병원인 경우, 정보가 업데이트 됐을 수도 있으니 전화번호, 위경도 수정
                Optional<PetHospital> existingPetHospital = petHospitalRepository.findByNameAndAddress(row.getName(), row.getAddress());
                if (existingPetHospital.isPresent()) {
                    PetHospital petHospital = existingPetHospital.get();
                    if (petHospital.getLatestAt() != null && row.getLatestAt() != null &&
                            petHospital.getLatestAt().isBefore(row.getLatestAt())) {
                        petHospital.update(row.getTel(), wgs84.lat(), wgs84.lng(), row.getLatestAt());
                        continue;
                    }
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
                        .latestAt(row.getLatestAt())
                        .build();
                savePetHospital.add(petHospital);
            }
            petHospitalRepository.saveAll(savePetHospital);
        }
        Files.deleteIfExists(csvPath);
        log.info("AdminPetHospitalService - saveFromCsv(): Successful Save ALL.");
    }

    private static void verifyingPermissionsAdmin(Member admin) throws AccessDeniedException {
        if (!admin.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("해당 기능은 관리자 권한이 필요합니다.");
        }
    }

    private Path getCsvPath(MultipartFile file) throws IOException {
        Path uploadDir = Paths.get(petHospitalUploadPath);
        Files.createDirectories(uploadDir); // 부모 디렉터리가 없어도 알아서 함께 만들고, 이미 있는 경우 그냥 넘어감(createDirectory() 얘는 디렉토리 1개만 생성 가능하고(부모 디렉토리가 무조건 있는 경우 씀), 이미 있는 경우 예외 터짐)

        Path csvPath = Files.createTempFile(uploadDir, "pet-hospital-", ".csv"); // 임시 파일 생성(파일명은 임의로 자동으로 생성되는데 prefix로 시작 suffix로 끝, 생성된 임시 파일의 경로 반환)
        file.transferTo(csvPath.toFile()); // 업로드된 파일을 tempPath에 저장(.toFile() -> Path 객체를 File 객체로 변환)
        log.info("AdminPetHospitalService - getCsvPath(): csvPath={}", csvPath);
        return csvPath;
    }

    /**
     * CSV -> 객체 파싱 메서드
     */
    private static List<PetHospitalDTO> parsingPetHospitalDTO(Reader reader, HeaderColumnNameMappingStrategy<PetHospitalDTO> strategy) {
        return new CsvToBeanBuilder<PetHospitalDTO>(reader)
                .withMappingStrategy(strategy)
                .withIgnoreLeadingWhiteSpace(true)
                .withQuoteChar('"') // csv 파일 헤더의 "" <- 쌍 따옴표 제거
                .build()
                .parse();
    }

    /**
     * 동물 병원 전체 조회
     */
    public List<PetHospitalDTO> getPetHospitalList() {
        return null;
    }
}
