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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PetRepository petRepository;

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

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));

        if (member.getRole().equals(Role.CUSTOMER)) {
            return new MemberResponse.GetCustomer(member);
        } else if (member.getRole().equals(Role.PET_SITTER)) {
            return new MemberResponse.GetSitter(member);
        } else if (member.getRole().equals(Role.ADMIN)) {
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

        memberRepository.delete(member);
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
