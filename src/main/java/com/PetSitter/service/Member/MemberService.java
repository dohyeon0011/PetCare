package com.PetSitter.service.Member;

import com.PetSitter.config.exception.BadRequestCustom;
import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.Role;
import com.PetSitter.domain.Member.SocialProvider;
import com.PetSitter.domain.UploadFile;
import com.PetSitter.dto.Member.request.AddMemberRequest;
import com.PetSitter.dto.Member.request.UpdateMemberRequest;
import com.PetSitter.dto.Member.response.MemberResponse;
import com.PetSitter.repository.Member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public Object save(AddMemberRequest request, UploadFile uploadFile) {
        validateDuplicateMember(request);

        String encodedPassword = bCryptPasswordEncoder.encode(request.getPassword());

        Member member;

        if (uploadFile != null) { // 프로필 이미지를 등록 했을 때
            member = request.toEntity(encodedPassword, uploadFile.getServerFileName());
        } else {
            member = request.toEntity(encodedPassword, null); // 프로필 이미지를 등록하지 않았을 때
        }

        return memberRepository.save(member).toResponse();
    }

    @Transactional(readOnly = true)
    public Object findById(long id) {
        /*Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));

        return member.toResponse();*/

        Role role = memberRepository.findRoleById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));

        if (role.equals(Role.CUSTOMER)) {
            Member member = memberRepository.findByCustomerId(id, role)
                    .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));

            return new MemberResponse.GetCustomer(member, member.getPets().stream()
                    .filter(pet -> !pet.isDeleted())
                    .collect(Collectors.toList()));
        } else if (role.equals(Role.PET_SITTER)) {
            Member member = memberRepository.findBySitterId(id, role)
                    .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));

//            return new MemberResponse.GetSitter(member, member.getCertifications());
            return member.toResponse();
        } else if (role.equals(Role.ADMIN)) {
            return new MemberResponse();
        }

        throw new NoSuchElementException("존재하지 않는 회원입니다.");
    }

    // 회원 + 보유중인 반려견 목록 조회
//    public List<Pet> findPetsByMemberId(long id) {
//        Member member = memberRepository.findById(id).
//                orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));
//
//        return memberRepository.findPetsByMemberId(member.getId());
//    }

    @Transactional
    public void delete(long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));

        authorizetionMember(member);

        member.changeIsDeleted(true); // 논리적으로 탈퇴 처리(직접 리포지토리에서 쿼리 날리면 update 쿼리문 최적화 가능(실제 update 하는 것만 하면 되니 -> 변경 감지 없이 직업 sql을 실행해서), 이 경우에는 객체 지향적이지만 쿼리문 최적화 불가능(필드들 다 update 쿼리 날라감.))
    }

    @Transactional
    public Object update(long id, UpdateMemberRequest request, UploadFile uploadFile) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));

        authorizetionMember(member);

        // 소셜 로그인 회원이 아닌 경우에만 비밀번호 입력 필수 처리
        if ((member.getSocialProvider() == null || member.getSocialProvider().equals(SocialProvider.NONE)) && (request.getPassword() == null || request.getPassword().trim().isEmpty())) {
            throw new BadRequestCustom("비밀번호 입력은 필수입니다.(최소 8자 입력 필요)");
        }

        System.out.println("Social Provider: " + member.getSocialProvider());
        System.out.println("Password: " + request.getPassword());


        String password = null;
        if (request.getPassword() != null) {
            password = bCryptPasswordEncoder.encode(request.getPassword());
        }

        member.update(
                password, request.getName(), request.getNickName(), request.getEmail(),
                request.getPhoneNumber(), request.getZipcode(), request.getAddress(),
                uploadFile != null ? uploadFile.getServerFileName() : null,
                request.getIntroduction(), request.getCareerYear(), request.getRole()
        );

        return member.toResponse();
    }

    @Comment("리뷰가 가장 많은 대표 돌봄사 최상위 3명 조회(메인 페이지 - 자세히 보기 3 (어떤 분들이 활동하고 있나요?) 안내)")
    @Transactional(readOnly = true)
    public Object findBestSitters() {
        PageRequest pageable = PageRequest.of(0, 3);

        return memberRepository.findBestSitters(pageable)
                .stream()
                .map(Member::toResponse)
                .collect(Collectors.toList());
    }

    private static void authorizetionMember(Member member) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName(); // 로그인에 사용된 아이디 값 반환

        if(!member.getLoginId().equals(userName)) {
            throw new IllegalArgumentException("회원 탈퇴 오류: 회원 본인만 가능합니다.");
        }
    }

    private void validateDuplicateMember(AddMemberRequest request) {
        Optional<Member> member = memberRepository.findByLoginId(request.getLoginId());

        if (!member.isEmpty()) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }
    }

}
