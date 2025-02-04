package com.PetSitter.service.CareAvailableDate;

import com.PetSitter.domain.CareAvailableDate.CareAvailableDate;
import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.Role;
import com.PetSitter.dto.CareAvailableDate.request.AddCareAvailableDateRequest;
import com.PetSitter.dto.CareAvailableDate.request.UpdateCareAvailableDateRequest;
import com.PetSitter.dto.CareAvailableDate.response.CareAvailableDateResponse;
import com.PetSitter.repository.CareAvailableDate.CareAvailableDateRepository;
import com.PetSitter.repository.Member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CareAvailableDateService {

    private final MemberRepository memberRepository;
    private final CareAvailableDateRepository careAvailableDateRepository;

    @Transactional
    public CareAvailableDate save(long sitterId, AddCareAvailableDateRequest request) {
        Member sitter = memberRepository.findById(sitterId)
                .orElseThrow(() -> new NoSuchElementException("돌봄 날짜 등록 오류: 현재 회원은 존재하지 않는 회원입니다."));

        verifyingPermissions(sitter);

        CareAvailableDate careAvailableDate = request.toEntity();
        careAvailableDate.addPetSitter(sitter);

        return careAvailableDateRepository.save(careAvailableDate);
    }

    @Comment("모든 회원의 돌봄 가능 날짜 조회")
    @Transactional(readOnly = true)
    public List<CareAvailableDateResponse.GetList> findAll() {
        List<CareAvailableDateResponse.GetList> careAvailableDateList = careAvailableDateRepository.findAll()
                .stream()
                .map(CareAvailableDateResponse.GetList::new)
                .collect(Collectors.toList());

        return careAvailableDateList;
    }

    @Comment("등록한 돌봄 가능 날짜 조회")
    @Transactional(readOnly = true)
    public Page<CareAvailableDateResponse.GetList> findAllById(long sitterId, Pageable pageable) {
//        List<CareAvailableDateResponse.GetList> careAvailableDateList = careAvailableDateRepository.findBySitterId(sitterId)
//                .stream()
//                .map(CareAvailableDateResponse.GetList::new)
//                .collect(Collectors.toList());

//        return careAvailableDateList;

        Page<CareAvailableDateResponse.GetList> careAvailableDates = careAvailableDateRepository.findBySitterId(sitterId, pageable);

        return careAvailableDates;
    }

    @Comment("등록한 돌봄 가능 날짜 단건 조회")
    @Transactional(readOnly = true)
    public CareAvailableDateResponse.GetDetail findById(long sitterId, long careAvailableDateId) {
        /*CareAvailableDate careAvailableDate = careAvailableDateRepository.findBySitterIdAndId(sitterId, careAvailableDateId)
                .orElseThrow(() -> new NoSuchElementException("등록한 돌봄 날짜가 존재하지 않습니다."));

        return new CareAvailableDateResponse.GetDetail(careAvailableDate);*/

        CareAvailableDateResponse.GetDetail careAvailableDate = careAvailableDateRepository.findBySitterIdAndIdDetail(sitterId, careAvailableDateId)
                .orElseThrow(() -> new NoSuchElementException("등록한 돌봄 날짜가 존재하지 않습니다."));

        return careAvailableDate;
    }

    @Comment("등록한 돌봄 가능 날짜 삭제")
    @Transactional
    public void delete(long memberId, long careAvailableDateId) {
        Member sitter = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("회원 정보를 불러오는데 실패했습니다."));

        verifyingPermissions(sitter);
        authorizetionMember(sitter);

        CareAvailableDate careAvailableDate = careAvailableDateRepository.findBySitterIdAndId(sitter.getId(), careAvailableDateId)
                .orElseThrow(() -> new NoSuchElementException("등록한 돌봄 날짜가 존재하지 않습니다."));

        careAvailableDateRepository.delete(careAvailableDate);
    }

    @Comment("등록한 돌봄 가능 정보 수정")
    @Transactional
    public CareAvailableDateResponse.GetDetail update(long sitterId, long careAvailableDateId, UpdateCareAvailableDateRequest request) {
        Member sitter = memberRepository.findById(sitterId)
                .orElseThrow(() -> new NoSuchElementException("회원 정보를 불러오는데 실패했습니다."));

        verifyingPermissions(sitter);
        authorizetionMember(sitter);

        CareAvailableDate careAvailableDate = careAvailableDateRepository.findBySitterIdAndId(sitter.getId(), careAvailableDateId)
                .orElseThrow(() -> new NoSuchElementException("등록한 돌봄 날짜가 존재하지 않습니다."));

        careAvailableDate.update(request.getAvailabilityAt(), request.getPrice());

//        return new CareAvailableDateResponse.GetDetail(careAvailableDate);

        // 추가적인 DB 쿼리가 발생하지 않는 대신에(영속성 컨텍스트에 이미 올라가져 있으니) 불필요한 데이터가 조회되고, 영속성 컨텍스트에 의존해서 트랜잭션 내에서만 유효하게 됨.
        // 성능적인 측면으로 봤을 때는 이 방법이 좋긴 함.
        /*return new CareAvailableDateResponse.GetDetail(
                careAvailableDate.getId(),
                careAvailableDate.getAvailableAt(),
                careAvailableDate.getPrice(),
                careAvailableDate.getSitter().getZipcode(),
                careAvailableDate.getSitter().getAddress(),
                careAvailableDate.getStatus()
        );*/

        // 수정된 데이터 다시 조회
        // 새로운 DB 쿼리가 발생하지만 엔티티를 수정한 후, 최신 상태를 DB에서 직접 조회하기 때문에 데이터의 정확성 보장 가능.
        CareAvailableDateResponse.GetDetail updateCareAvailableDate = careAvailableDateRepository.findBySitterIdAndIdDetail(sitter.getId(), careAvailableDateId)
                .orElseThrow(() -> new NoSuchElementException("등록된 돌봄 날짜가 존재하지 않습니다."));

        return updateCareAvailableDate;
    }

    private static void authorizetionMember(Member member) {
//        String userName = SecurityContextHolder.getContext().getAuthentication().getName(); // 로그인에 사용된 아이디 값 반환
//
//        if(!member.getLoginId().equals(userName)) {
//            throw new IllegalArgumentException("회원 본인만 가능합니다.");
//        }
    }

    public static void verifyingPermissions(Member member) {
        if (!member.getRole().equals(Role.PET_SITTER)) {
            throw new IllegalArgumentException("돌봄사만 돌봄 날짜 등록 및 수정,삭제가 가능합니다.");
        }
    }

}
