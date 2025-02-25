package com.PetSitter.service.Member;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.Role;
import com.PetSitter.dto.Member.request.AddMemberRequest;
import com.PetSitter.dto.Member.request.UpdateMemberRequest;
import com.PetSitter.dto.Member.response.MemberResponse;
import com.PetSitter.repository.Member.MemberRepository;
import com.PetSitter.repository.Pet.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Object save(AddMemberRequest request) {
        validateDuplicateMember(request);

        return memberRepository.save(request.toEntity()).toResponse();
    }

    @Transactional(readOnly = true)
    public List<Object> findAll() {
        List<Member> members = memberRepository.findAll();

        return members.stream()
                .map(Member::toResponse)
                .toList();
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

//        memberRepository.delete(member);

        if (member.isDeleted()) {
            throw new IllegalArgumentException("이미 탈퇴 처리된 회원입니다.");
        }

        member.changeIsDeleted(true); // 논리적으로 탈퇴 처리(직접 리포지토리에서 쿼리 날리면 update 쿼리문 최적화 가능(실제 update 하는 것만 하면 되니 -> 변경 감지 없이 직업 sql을 실행해서), 이 경우에는 객체 지향적이지만 쿼리문 최적화 불가능(필드들 다 update 쿼리 날라감.))
    }

    @Transactional
    public Object update(long id, UpdateMemberRequest request) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));

        authorizetionMember(member);

        member.update(
                request.getPassword(), request.getName(), request.getNickName(), request.getEmail(),
                request.getPhoneNumber(), request.getZipcode(), request.getAddress(),
                request.getProfileImgPath(), request.getIntroduction(), request.getCareerYear()
        );

        return member.toResponse();
    }

    /*@Transactional(readOnly = true)
    public List<MemberResponse.GetSitter> getSitters() {
        memberRepository.findAllSitter();
    }*/

    private static void authorizetionMember(Member member) {
//        String userName = SecurityContextHolder.getContext().getAuthentication().getName(); // 로그인에 사용된 아이디 값 반환
//
//        if(!member.getLoginId().equals(userName)) {
//            throw new IllegalArgumentException("회원 본인만 가능합니다.");
//        }
    }

    private void validateDuplicateMember(AddMemberRequest request) {
        Optional<Member> member = memberRepository.findByLoginId(request.getLoginId());

        if (!member.isEmpty()) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }
    }

}
