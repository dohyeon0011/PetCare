package com.PetSitter.service.Certification;

import com.PetSitter.domain.Certification.Certification;
import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.Role;
import com.PetSitter.dto.Certification.request.AddCertificationRequest;
import com.PetSitter.dto.Certification.request.UpdateCertificationRequest;
import com.PetSitter.dto.Certification.response.CertificationResponse;
import com.PetSitter.repository.Certification.CertificationRepository;
import com.PetSitter.repository.Member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Comment;
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

        return certificationRepository.saveAll(certifications);
    }

    @Comment("특정 회원의 보유중인 자격증 조회")
    @Transactional(readOnly = true)
    public List<CertificationResponse.GetList> findById(long sitterId) {
        List<CertificationResponse.GetList> certifications = certificationRepository.findBySitterId(sitterId)
                .stream()
                .map(CertificationResponse.GetList::new)
                .collect(Collectors.toList());

        return certifications;
    }

    @Comment("특정 회원의 보유중인 특정 자격증 삭제")
    @Transactional
    public void delete(long sitterId, long certificationId) {
        Member sitter = memberRepository.findById(sitterId)
                .orElseThrow(() -> new NoSuchElementException("회원 정보를 불러오는데 실패했습니다."));

        verifyingPermissions(sitter);
        authorizetionMember(sitter);

        Certification certification = certificationRepository.findBySitterIdAndId(sitter.getId(), certificationId)
                .orElseThrow(() -> new NoSuchElementException("등록한 자격증이 존재하지 않습니다."));

        certificationRepository.delete(certification);
    }

    @Comment("특정 회원의 보유중인 자격증 수정")
    @Transactional
    public List<CertificationResponse.GetList> update(long sitterId, List<UpdateCertificationRequest> requests) {
        Member sitter = memberRepository.findById(sitterId)
                .orElseThrow(() -> new NoSuchElementException("회원 정보를 불러오는데 실패했습니다."));

        verifyingPermissions(sitter);
        authorizetionMember(sitter);

        List<Certification> certifications = certificationRepository.findBySitterId(sitterId);

        if (certifications == null || certifications.isEmpty()) {
            throw new NoSuchElementException("해당 회원의 자격증이 존재하지 않습니다.");
        }

        for (UpdateCertificationRequest request : requests) {
            System.out.println("Request ID: " + request.getId());
            certifications.forEach(cert -> System.out.println("Certification ID: " + cert.getId()));

            Certification certification = certifications.stream()
                    .filter(c -> c.getId() == request.getId())
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("자격증 조회에 실패했습니다."));

            certification.update(request.getName());
        }

        return certifications.stream()
                .map(CertificationResponse.GetList::new)
                .toList();
    }

    private static void authorizetionMember(Member member) {
//        String userName = SecurityContextHolder.getContext().getAuthentication().getName(); // 로그인에 사용된 아이디 값 반환
//
//        if(!member.getLoginId().equals(userName)) {
//            throw new IllegalArgumentException("회원 본인만 가능합니다.");
//        }
    }

    private static void verifyingPermissions(Member member) {
        if (!member.getRole().equals(Role.PET_SITTER)) {
            throw new IllegalArgumentException("돌봄사만 자격증 등록,수정 및 삭제가 가능합니다.");
        }
    }

}
