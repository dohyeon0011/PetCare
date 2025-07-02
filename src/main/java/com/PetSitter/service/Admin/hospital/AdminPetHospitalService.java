package com.PetSitter.service.Admin.hospital;

import com.PetSitter.config.annotation.ReadOnlyTransactional;
import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.Role;
import com.PetSitter.domain.hospital.PetHospital;
import com.PetSitter.domain.hospital.PetHospitalSearch;
import com.PetSitter.dto.hospital.response.PetHospitalDTO;
import com.PetSitter.repository.hospital.PetHospitalRepository;
import com.PetSitter.util.coordinate.CoordinateConverter;
import com.PetSitter.util.coordinate.LatLng;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
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
import java.sql.Timestamp;
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
    @Transactional(rollbackFor = IOException.class) // CSV 파일 못 읽을 경우 트랜잭션 롤백 되게 설정(기본은 RuntimeException, 일반 error만 된다 함.), 항상 해당 메서드의 바깥 외부로 던져야 롤백이 됨.
    public void saveFromCsv(MultipartFile file, Member admin) throws IOException {
        log.info("AdminPetHospitalService - saveFromCsv(): 호출 성공.");
        verifyingPermissionsAdmin(admin);
        Path csvPath = getCsvPath(file);

        // try-with-resources -> try 문 종료 후, 선언부에 생성된 객체 알아서 닫아줌.
        try (Reader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8)) {    // CSV 파일을 ISO_8859_1 인코딩 방식으로 읽는 Reader 객체 생성
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
                /*if (row.getLat() == null || row.getLng() == null) {
                    log.warn("위도/경도 value is null. lat={}, lng={}", row.getLat(), row.getLng());
                    continue;
                }*/
                log.info("CSV -> DTO 파싱 결과: name={}, lat={}, lng={}", row.getName(), row.getLat(), row.getLng());

                // 좌표 변환: EPSG:5174 -> WGS84(지도에 마커 표시를 위해)
                LatLng wgs84 = null;
                if (row.getLat() != null && row.getLng() != null) {
                    wgs84 = CoordinateConverter.convertTMToWGS84(row.getLat(), row.getLng());
                }
                // 이미 정보가 있는 병원인 경우, 정보가 업데이트 됐을 수도 있으니 전화번호, 위경도 수정
                Optional<PetHospital> existingPetHospital = petHospitalRepository.findByNameAndAddress(row.getName(), row.getAddress());
                if (existingPetHospital.isPresent()) {
                    PetHospital petHospital = existingPetHospital.get();
                    if (petHospital.getLatestAt() != null && row.getLatestAt() != null &&
                            petHospital.getLatestAt().isBefore(row.getLatestAt())) {
                        petHospital.update(row.getTel(),
                                wgs84 != null ? wgs84.lat() : petHospital.getLat(),
                                wgs84 != null ? wgs84.lng() : petHospital.getLng(),
                                row.getLatestAt());
                        continue;
                    }
                }
                // 새로운 병원일 때
                mapAndAddPetHospital(row, wgs84, savePetHospital);
            }
            petHospitalRepository.saveAll(savePetHospital);
            log.info("총 파싱된 병원 수: {}", petHospitalDTOList.size());
        }
        Files.deleteIfExists(csvPath);
        deleteAllPetHospitalsCache();
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
     * 저장할 동물 병원 정보 DTO -> Entity로 매핑 메서드
     * @param savePetHospital: 새롭게 저장할 Entity를 담는 List
     */
    private static void mapAndAddPetHospital(PetHospitalDTO row, LatLng wgs84, List<PetHospital> savePetHospital) {
        PetHospital petHospital = PetHospital.builder()
                .name(row.getName())
                .zipcode(row.getZipcode())
                .address(row.getAddress())
                .streetZipcode(row.getStreetZipcode())
                .streetAddress(row.getStreetAddress())
                .tel(row.getTel())
                .lat(wgs84 != null ? wgs84.lat() : null)
                .lng(wgs84 != null ? wgs84.lng() : null)
                .latestAt(row.getLatestAt())
                .build();
        savePetHospital.add(petHospital);
    }

    /**
     * 동물 병원 전체 조회
     * @ReadOnlyTransactional: 커스텀 읽기 전용 어노테이션
     * 자동으로 Redis에 Cache Aside 읽기 전략으로 저장됨.(RedisCacheManager를 이용, value=캐시 이름(Redis에서는 Hash 이름처럼 동작) -> ex) petHospitalList::서울특별시:용산구:0)
     * @Cacheable unless = false일 때만 결과를 캐시에 저장, condition = true일 때만 캐시에 저장
     */
    @Cacheable(
            value = "petHospitalList",
            key = "T(org.apache.commons.lang3.StringUtils).defaultIfEmpty(#search.sido, 'ALL') + ':' + T(org.apache.commons.lang3.StringUtils).defaultIfEmpty(#search.sigungu, 'ALL') + ':' + #pageable.pageNumber", // 검색 조건이 없는 경우 null 처리 위해 -> ALL로 바꿔 저장
            unless = "#result == null || #result.isEmpty()" // condition: 메서드 호출 전 검증(특정 파라미터 조건일 때만 캐싱 한다던가), unless: 메서드 호출 후 결과 값에 따라 캐싱 (result는 SpEL(Spring Expression Language)에 예약된 변수임)
    )
    @ReadOnlyTransactional
    public List<PetHospitalDTO> getPetHospitalList(Pageable pageable, PetHospitalSearch search) {
        log.info("AdminPetHospitalService - getPetHospitalList(): " +
                        "Cache Miss로 DB 직접 조회 - key= {}:{}:{}", StringUtils.defaultIfEmpty(search.getSido(), "ALL"), StringUtils.defaultIfEmpty(search.getSigungu(), "ALL"), pageable.getPageNumber());
        int pageSize = pageable.getPageSize();
        int offset = (int) pageable.getOffset();

        List<Object[]> result = petHospitalRepository.findPageLatestPetHospitalsByNameAndAddressUsingNativeQuery(pageSize, offset, search.getSido(), search.getSigungu());

        return result.stream()
                .map(row -> new PetHospitalDTO(
                        (String) row[0],
                        row[1] != null ? (String) row[1] : null,
                        (String) row[2],
                        row[3] != null ? (String) row[3] : null,
                        row[4] != null ? (String) row[4] : null,
                        row[5] != null ? (String) row[5] : null,
                        row[6] != null ? ((Number) row[6]).doubleValue() : null,
                        row[7] != null ? ((Number) row[7]).doubleValue() : null,
                        row[8] != null ? ((Timestamp) row[8]).toLocalDateTime() : null
                ))
                .toList();
    }

    /**
     * 동물 병원 목록 조회 시 카운트 쿼리 메서드(요소 총 개수)
     * 동물 병원 목록 조회 데이터와 같은 생명 주기로 캐싱
     */
    @Cacheable(
            value = "petHospitalListCount",
            key = "T(org.apache.commons.lang3.StringUtils).defaultIfEmpty(#search.sido, 'ALL') + ':' + T(org.apache.commons.lang3.StringUtils).defaultIfEmpty(#search.sigungu, 'ALL')",
            unless = "#result == null"
    )
    public int getTotalCountElementsQuery(PetHospitalSearch search) {
        log.info("AdminPetHospitalService - getTotalCountElementsQuery(): " +
                "Cache Miss로 DB 직접 카운트 쿼리 조회 - key= {}:{}", StringUtils.defaultIfEmpty(search.getSido(), "ALL"), StringUtils.defaultIfEmpty(search.getSigungu(), "ALL"));
        return petHospitalRepository.countGroupedHospitals(search.getSido(), search.getSigungu());
    }

    /**
     * 병원 데이터가 갱신되었을 때, 이 메서드를 호출하면 petHospitalList와 petHospitalListCount 캐시를 모두 삭제 시키는 메서드
     * allEntries: 모든 키를 제거할 지 말지 여부
     */
    @CacheEvict(value = {"petHospitalList", "petHospitalListCount"}, allEntries = true)
    public void deleteAllPetHospitalsCache() {
    }
}
