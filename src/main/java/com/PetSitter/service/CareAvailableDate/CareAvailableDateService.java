package com.PetSitter.service.CareAvailableDate;

import com.PetSitter.config.annotation.ReadOnlyTransactional;
import com.PetSitter.domain.CareAvailableDate.CareAvailableDate;
import com.PetSitter.domain.CareAvailableDate.CareAvailableDateStatus;
import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.Role;
import com.PetSitter.dto.CareAvailableDate.request.AddCareAvailableDateRequest;
import com.PetSitter.dto.CareAvailableDate.request.UpdateCareAvailableDateRequest;
import com.PetSitter.dto.CareAvailableDate.response.CareAvailableDateResponse;
import com.PetSitter.repository.CareAvailableDate.CareAvailableDateRepository;
import com.PetSitter.repository.Member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CareAvailableDateService {

    private final MemberRepository memberRepository;
    private final CareAvailableDateRepository careAvailableDateRepository;

    /**
     *  돌봄사: 돌봄 가능 날짜 등록
     */
    public CareAvailableDateResponse.GetDetail save(long sitterId, AddCareAvailableDateRequest request) {
        Member sitter = memberRepository.findByIdWithCareAvailableDates(sitterId)
                .orElseThrow(() -> new NoSuchElementException("돌봄 날짜 등록 오류: 현재 회원은 존재하지 않는 회원입니다."));
        verifyingPermissions(sitter);

        if (request.getAvailableAt().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("현재 날짜보다 이전 날짜는 등록할 수 없습니다.");
        }

        if (careAvailableDateRepository.findBySitterId(sitterId).stream().anyMatch(c -> c.getAvailableAt().equals(request.getAvailableAt()))) {
            throw new IllegalArgumentException("이미 돌봄 등록한 날짜입니다. 날짜를 다시 선택해 주세요.");
        }
        CareAvailableDate careAvailableDate = request.toEntity();
        careAvailableDate.addPetSitter(sitter);

        careAvailableDateRepository.saveAndFlush(careAvailableDate); // 단건 insert는 @Transactional 제거 후 직접 db에 flush가 비용이 쌈. 대신, 영속성 컨텍스트 관리 비용이 들어감.(jpql or native query로도 가능.)

        return careAvailableDateRepository.findBySitterIdAndIdDetail(sitterId, careAvailableDate.getId())
                .orElseThrow(() -> new NoSuchElementException("등록한 돌봄 날짜가 존재하지 않습니다."));
    }

    /**
     * 모든 회원의 돌봄 가능 날짜 조회
     * @ReadOnlyTransactional: 커스텀 읽기 전용 어노테이션
     */
    @ReadOnlyTransactional
    public List<CareAvailableDateResponse.GetList> findAll() {
        return careAvailableDateRepository.findAll()
                .stream()
                .map(CareAvailableDateResponse.GetList::new)
                .collect(Collectors.toList());
    }

    /**
     * 돌봄사: 등록한 돌봄 가능 날짜 조회
     * @ReadOnlyTransactional: 커스텀 읽기 전용 어노테이션
     */
    @ReadOnlyTransactional
    public Page<CareAvailableDateResponse.GetList> findAllById(long sitterId, Pageable pageable) {
        return careAvailableDateRepository.findBySitterId(sitterId, pageable);
    }

    /**
     * 돌봄사: 등록한 돌봄 가능 날짜 단건 조회
     * @ReadOnlyTransactional: 커스텀 읽기 전용 어노테이션
     */
    @ReadOnlyTransactional
    public CareAvailableDateResponse.GetDetail findById(long sitterId, long careAvailableDateId) {
        return careAvailableDateRepository.findBySitterIdAndIdDetail(sitterId, careAvailableDateId)
                .orElseThrow(() -> new NoSuchElementException("등록한 돌봄 날짜가 존재하지 않습니다."));
    }

    /**
     * 돌봄사: 등록한 돌봄 가능 날짜 삭제
     */
    @Transactional
    public void delete(long memberId, long careAvailableDateId) {
        Member sitter = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("회원 정보를 불러오는데 실패했습니다."));
        authorizationMember(sitter);
        verifyingPermissions(sitter);

        CareAvailableDate findCareAvailableDate = careAvailableDateRepository.findBySitterIdAndId(memberId, careAvailableDateId)
                .orElseThrow(() -> new NoSuchElementException("등록한 돌봄 날짜가 존재하지 않습니다."));

        if (!findCareAvailableDate.getStatus().equals(CareAvailableDateStatus.POSSIBILITY)) {
            throw new IllegalArgumentException("해당 돌봄 날짜가 현재 예약에 배정된 상태이므로 삭제가 불가합니다. (해당 돌봄 날짜: " + findCareAvailableDate.getAvailableAt() + ")");
        }
        careAvailableDateRepository.delete(findCareAvailableDate); // SimpleJpaRepository에서 delete 메서드가 @Transactional이 걸려있음.(트랜잭션 안 쓰려면 오버라이딩 해야됨.)
    }

    /**
     * 돌봄사: 등록한 돌봄 가능 정보 수정
     */
    @Transactional
    public CareAvailableDateResponse.GetDetail update(long sitterId, long careAvailableDateId, UpdateCareAvailableDateRequest request) {
        Member sitter = memberRepository.findById(sitterId)
                .orElseThrow(() -> new NoSuchElementException("회원 정보를 불러오는데 실패했습니다."));
        authorizationMember(sitter);
        verifyingPermissions(sitter);

        if (request.getAvailableAt().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("현재 날짜보다 이전 날짜는 등록할 수 없습니다.");
        }
        CareAvailableDate findCareAvailableDate = careAvailableDateRepository.findBySitterIdAndId(sitterId, careAvailableDateId)
                .orElseThrow(() -> new NoSuchElementException("등록한 돌봄 날짜가 존재하지 않습니다."));

        // 이미 등록된 돌봄 날짜가 현재 수정하려는 날짜와 동일하지 않은지 확인
        if (careAvailableDateRepository.findBySitterId(sitterId).stream()
                .anyMatch(c -> c.getId() != careAvailableDateId && c.getAvailableAt().equals(request.getAvailableAt()))) {
            throw new IllegalArgumentException("이미 돌봄 등록한 날짜입니다. 날짜를 다시 선택해 주세요.");
        }
        findCareAvailableDate.update(request.getAvailableAt(), request.getPrice(), request.getStatus());

        return new CareAvailableDateResponse.GetDetail(
                findCareAvailableDate.getId(),
                findCareAvailableDate.getAvailableAt(),
                findCareAvailableDate.getPrice(),
                findCareAvailableDate.getSitter().getZipcode(),
                findCareAvailableDate.getSitter().getAddress(),
                findCareAvailableDate.getStatus()
        );
    }

    private static void authorizationMember(Member member) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName(); // 로그인에 사용된 아이디 값 반환
        if(!member.getLoginId().equals(userName)) {
            throw new IllegalArgumentException("회원 본인만 가능합니다.");
        }
    }

    public static void verifyingPermissions(Member member) {
        if (!member.getRole().equals(Role.PET_SITTER)) {
            throw new IllegalArgumentException("돌봄사만 돌봄 날짜 등록 및 수정,삭제가 가능합니다.");
        }
    }
}
