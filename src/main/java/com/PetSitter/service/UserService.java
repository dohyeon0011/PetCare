package com.PetSitter.service;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.repository.Member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final MemberRepository memberRepository;

    public Member authenticate(String loginId, String password) {
        Optional<Member> member = memberRepository.findByLoginId(loginId);

        if (!member.isEmpty() && member.get().getPassword().equals(password)) {
            return member.get(); // 인증 성공
        }

        return null; // 인증 실패
    }
}
