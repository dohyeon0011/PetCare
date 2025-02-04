/*
package com.PetSitter.repository.Member;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.Role;
import com.PetSitter.domain.Member.SocialProvider;
import com.PetSitter.dto.Member.request.AddMemberRequest;
import com.PetSitter.dto.Member.request.UpdateMemberRequest;
import com.PetSitter.repository.Member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@Rollback
// @ExtendWith(SpringExtension.class) Mock이나 Stub으로 의존성을 처리하고, Spring 컨텍스트를 로드하지 않는 테스트인 경우에 사용, SpringBootTest에 기본으로 달려 있음
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @BeforeEach
    public void setUp() {
        AddMemberRequest savedMember1 = AddMemberRequest.builder()
                .loginId("user1")
                .password("aaw131")
                .name("구창모")
                .nickName("창모")
                .email("email@naver.com")
                .phoneNumber("010-1234-5678")
                .zipcode("12345")
                .address("경기도")
                .role("CUSTOMER")
                .socialProvider(null)
                .introduction("하이요")
                .careerYear(null)
//                .certifications(Collections.singletonList("돌봄1급, 돌봄2급"))
                .build();

        AddMemberRequest savedMember2 = AddMemberRequest.builder()
                .loginId("user2")
                .password("aaw131")
                .name("윤진영")
                .nickName("애쉬아일랜드")
                .email("email@naver.com")
                .phoneNumber("010-1234-5678")
                .zipcode("22222")
                .address("서울")
                .role("PET_SITTER")
                .socialProvider("KAKAO")
                .introduction("하이요")
                .careerYear(5)
                .build();

        memberRepository.save(savedMember1.toEntity());
        memberRepository.save(savedMember2.toEntity());
    }

    @DisplayName("회원가입_엔티티로 가입")
    @Test
    public void member_join() {
        //given
        Member member = new Member("user1", "aaw131", "구창모", "창모", "email@naver.com", "01012345678", "123-456", "경기도", Role.CUSTOMER, null, null, null);
        memberRepository.save(member);

        //when
        Optional<Member> findMember = memberRepository.findById(member.getId());

        //then
        assertEquals(member, findMember.get()); // Optional에서 값 추출 후 비교
        assertThat(member.getName()).isEqualTo("구창모");
    }

    @DisplayName("회원가입_DTO로 가입")
    @Rollback(value = false)
    @Test
    public void member_join_dto() {
        //given
        // DTO에 @Builder 쓰면 이렇게 하고 안 쓸거면 바로 아래처럼.
        AddMemberRequest savedMember = AddMemberRequest.builder()
                .loginId("user1")
                .password("aaw131")
                .name("구창모")
                .nickName("창모")
                .email("email@naver.com")
                .phoneNumber("010-1234-5678")
                .zipcode("12345")
                .address("경기도")
                .role("CUSTOMER")
                .socialProvider(null)
                .introduction("하이요")
                .careerYear(3)
//                .certificates(Collections.singletonList("돌봄1급, 돌봄2급"))
                .build();


        */
/*AddMemberRequest savedMember = new AddMemberRequest(
                "user1",
                "aaw131",
                "구창모",
                "창모",
                "email@naver.com",
                "010-1234-5678",
                "12345",
                "경기도",
                "CUSTOMER",
                null,
                "하이",
                3,
                null
        );*//*



        //when
//        Member member = savedMember.toEntity();
//        memberRepository.save(member);
        Member member = memberRepository.findById(1L).get();

        //then
        Member findMember = memberRepository.findById(member.getId()).orElseThrow();
        assertEquals(findMember.getLoginId(), member.getLoginId());
        assertEquals(findMember.getNickName(), member.getNickName());
        assertThat(findMember.getEmail()).isEqualTo("email@naver.com");
        assertThat(findMember.getSocialProvider()).isEqualTo(SocialProvider.NONE);
        assertThat(findMember.getCareerYear()).isEqualTo(3);
//        assertThat(findMember.getCertifications()).containsExactly("돌봄1급, 돌봄2급");
        System.out.println("생성시간 : " + findMember.getCreatedAt());
    }

    @DisplayName("회원조회")
    @Test
    public void member_findByAll() {
        //when
        List<Member> findMembers = memberRepository.findAll();

        //then
        assertThat(findMembers.size()).isEqualTo(2);
    }

    @DisplayName("특정 회원조회")
    @Test
    public void member_findById() {
        //when
        Member member = memberRepository.findById(1L).get();

        //then
        assertThat(member.getId()).isEqualTo(1L);
        assertThat(member.getNickName()).isEqualTo("창모");
    }

    @DisplayName("회원 정보 수정")
    @Rollback(value = false)
    @Test
    public void member_update() {
        //given
        Member member = memberRepository.findById(1L).get();
        UpdateMemberRequest updateMemberRequest = new UpdateMemberRequest("psss1", "구창모", "changmo", "changmo@gmail.com", "010-1111-1111", "10222", "서울특별시 용산구 한남동", null, "언더그라운드락스타", null);

        //when
        member.update(
                updateMemberRequest.getPassword(), updateMemberRequest.getName(), updateMemberRequest.getNickName(), updateMemberRequest.getEmail(),
                updateMemberRequest.getPhoneNumber(), updateMemberRequest.getZipcode(), updateMemberRequest.getAddress(),
                updateMemberRequest.getProfileImgPath(), updateMemberRequest.getIntroduction(), updateMemberRequest.getCareerYear()
        );

        //then
        assertThat(member.getNickName()).isEqualTo("changmo");
    }

    @DisplayName("회원탈퇴")
    @Test
    public void member_deleteById () {
        //given
        memberRepository.deleteById(2L);

        //when
        List<Member> members = memberRepository.findAll();

        //then
        assertThat(members.size()).isEqualTo(1);
    }

}
*/
