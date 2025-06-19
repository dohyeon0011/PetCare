package com.PetSitter.service.Certification;

import com.PetSitter.config.annotation.ReadOnlyTransactional;
import com.PetSitter.domain.Certification.Certification;
import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.Role;
import com.PetSitter.dto.Certification.request.AddCertificationRequest;
import com.PetSitter.dto.Certification.request.UpdateCertificationRequest;
import com.PetSitter.dto.Certification.response.CertificationResponse;
import com.PetSitter.repository.Certification.CertificationRepository;
import com.PetSitter.repository.Member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CertificationService {

    private final CertificationRepository certificationRepository;
    private final MemberRepository memberRepository;

    /**
     * 돌봄사: 자격증 등록
     */
    @Transactional
    public List<Certification> save(Long sitterId, List<AddCertificationRequest> requests) {
        Member sitter = memberRepository.findById(sitterId)
                .orElseThrow(() -> new NoSuchElementException("자격증 등록 오류: 현재 회원은 존재하지 않는 회원입니다."));
        verifyingPermissions(sitter);

        List<Certification> certifications = new ArrayList<>();
        for (AddCertificationRequest request : requests) {
            Certification certification = request.toEntity();
            certification.addSitter(sitter);
            certifications.add(certification);
        }
        return certificationRepository.saveAllAndFlush(certifications);
    }

    /**
     * 돌봄사: 특정 회원의 보유중인 자격증 조회
     * @ReadOnlyTransactional: 커스텀 읽기 전용 어노테이션
     */
    @ReadOnlyTransactional
    public List<CertificationResponse.GetList> findById(long sitterId) {
        return certificationRepository.findBySitterId(sitterId)
                .stream()
                .map(CertificationResponse.GetList::new)
                .collect(Collectors.toList());
    }

    /**
     * 돌봄사: 특정 회원의 보유중인 특정 자격증 삭제
     */
    public void delete(long sitterId, long certificationId) {
        Member sitter = memberRepository.findById(sitterId)
                .orElseThrow(() -> new NoSuchElementException("회원 정보를 불러오는데 실패했습니다."));
        verifyingPermissions(sitter);
        authorizationMember(sitter);

        Certification findCertification = certificationRepository.findBySitterIdAndId(sitter.getId(), certificationId)
                .orElseThrow(() -> new NoSuchElementException("등록한 자격증이 존재하지 않습니다."));

        certificationRepository.delete(findCertification);
    }

    /**
     * 돌봄사: 특정 회원의 보유중인 자격증 수정
     */
    @Transactional
    public List<CertificationResponse.GetList> update(long sitterId, List<UpdateCertificationRequest> requests) {
        Member sitter = memberRepository.findById(sitterId)
                .orElseThrow(() -> new NoSuchElementException("회원 정보를 불러오는데 실패했습니다."));
        verifyingPermissions(sitter);
        authorizationMember(sitter);

        List<Certification> findCertifications = certificationRepository.findBySitterId(sitterId);
        if (findCertifications == null || findCertifications.isEmpty()) {
            throw new NoSuchElementException("해당 회원의 자격증이 존재하지 않습니다.");
        }

        for (UpdateCertificationRequest request : requests) {
            System.out.println("Request ID: " + request.getId());
            findCertifications.forEach(cert -> System.out.println("Certification ID: " + cert.getId()));

            Certification certification = findCertifications.stream()
                    .filter(c -> c.getId() == request.getId())
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("자격증 조회에 실패했습니다."));
            certification.update(request.getName());
        }
        return findCertifications.stream()
                .map(CertificationResponse.GetList::new)
                .toList();
    }

    private static void authorizationMember(Member member) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName(); // 로그인에 사용된 아이디 값 반환

        if(!member.getLoginId().equals(userName)) {
            throw new IllegalArgumentException("회원 본인만 가능합니다.");
        }
    }

    private static void verifyingPermissions(Member member) {
        if (!member.getRole().equals(Role.PET_SITTER)) {
            throw new IllegalArgumentException("돌봄사만 자격증 등록,수정 및 삭제가 가능합니다.");
        }
    }
}
