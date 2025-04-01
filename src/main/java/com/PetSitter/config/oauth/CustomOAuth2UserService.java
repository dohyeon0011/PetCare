package com.PetSitter.config.oauth;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.Role;
import com.PetSitter.domain.Member.SocialProvider;
import com.PetSitter.domain.Member.oauth.CustomOAuth2User;
import com.PetSitter.domain.Member.oauth.OAuth2UserInfo;
import com.PetSitter.domain.Member.oauth.OAuth2UserInfoFactory;
import com.PetSitter.repository.Member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    /**
     * CustomOAuth2UserService에서 사용자 정보를 가져올 때 OAuth2UserInfoFactory를 활용
     * OAuth2UserRequest: 소셜 로그인 요청을 담고 있는 객체 (구글, 카카오, 네이버 로그인 후 받은 정보 포함)
     * OAuth2User: 소셜 로그인 후 가져온 사용자 정보 객체
     */
    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException { // OAuth2 제공자로부터 사용자 정보를 불러오는 메서드
        OAuth2User oauth2User = new DefaultOAuth2UserService().loadUser(userRequest);
        log.info("getAttributes : {}", oauth2User.getAttributes());

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oauth2User.getAttributes());

        /*Member member = memberRepository.findByLoginIdAndIsDeletedFalse(userInfo.getId())    // 신규 사용자라면 자동 회원가입을 진행하고, 기존 회원이라면 기존 계정과 매칭해서 로그인
                .orElseGet(() -> registerNewMember(userInfo, registrationId));*/

        Optional<Member> existingMember = memberRepository.findByLoginId(userInfo.getId()); // 탈퇴 여부 상관없이 조회

        // 기존 회원일 때
        if (existingMember.isPresent()) {
            Member member = existingMember.get();

            // 기존에 탈퇴했던 회원일 때
            if (member.isDeleted()) {
                String newLoginId = member.getLoginId() + "_" + UUID.randomUUID().toString().substring(0, 8); // 기존 아이디의 로그인 아이디 임의 변경(새롭게 회원가입 할 로그인 아이디와 충돌이 나지 않게(유니크 키 제약 조건))
                memberRepository.updateLoginId(member.getId(), newLoginId);

                member = registerNewMember(userInfo, registrationId); // 회원 정보 다시 생성
            }

            return new CustomOAuth2User(member, oauth2User.getAttributes());
        }

        // 만약 기존 회원이 아니었다면, 새로운 회원으로 등록
        Member member = existingMember.orElseGet(() -> registerNewMember(userInfo, registrationId));

        return new CustomOAuth2User(member, oauth2User.getAttributes());
    }

    private Member registerNewMember(OAuth2UserInfo userInfo, String provider) {
        Member member = Member.builder()
                .loginId(userInfo.getId())  // OAuth2 로그인 ID
                .password(UUID.randomUUID().toString().substring(0, 18))
                .name(userInfo.getName())   // 사용자 이름
                .nickName(userInfo.getName() + "_" + UUID.randomUUID().toString().substring(0, 10)) // 사용자 닉네임
                .email(userInfo.getEmail()) // 이메일
                .phoneNumber(userInfo.getPhoneNumber())
                .zipcode("")
                .address("")
                .socialProvider(SocialProvider.valueOf(provider.toUpperCase())) // 제공자 (GOOGLE, NAVER, KAKAO)
                .role(Role.CUSTOMER)  // 기본 역할을 CUSTOMER로 설정 (필요시 수정)
                .build();

        return memberRepository.save(member);
    }
}
