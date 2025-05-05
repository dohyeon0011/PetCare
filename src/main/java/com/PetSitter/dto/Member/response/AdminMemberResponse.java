package com.PetSitter.dto.Member.response;

import com.PetSitter.domain.Certification.Certification;
import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.Role;
import com.PetSitter.domain.Member.SocialProvider;
import com.PetSitter.domain.Pet.Pet;
import com.PetSitter.dto.Certification.response.CertificationResponse;
import com.PetSitter.dto.Pet.response.PetResponse;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// 관리자 페이지(회원 조회)
@Schema(description = "관리자 페이지(회원 조회) Response DTO")
public class AdminMemberResponse {

    @NoArgsConstructor
    @Getter
    @Schema(description = "관리자 페이지(회원) 리스트 조회 Response DTO")
    public static class MemberListResponse {
        @Schema(description = "회원 id")
        private long id;

        @Schema(description = "회원 이름(실명)")
        private String name;

        @Schema(description = "회원 활동 닉네임")
        private String nickName;

        @Schema(description = "이메일")
        private String email;

        @Schema(description = "회원 역할", defaultValue = "CUSTOMER", pattern = "ex) CUSTOMER(고객), PET_SITTER(돌봄사)")
        private Role role;

        @Schema(description = "회원가입일자")
        private LocalDateTime createdAt;

        @Schema(description = "회원 탈퇴 여부", defaultValue = "false", pattern = "ex) false(활동중), true(탈퇴)")
        private boolean isDeleted;

        @QueryProjection
        public MemberListResponse(long id, String name, String nickName, String email, Role role, LocalDateTime createdAt, boolean isDeleted) {
            this.id = id;
            this.name = name;
            this.nickName = nickName;
            this.email = email;
            this.role = role;
            this.createdAt = createdAt;
            this.isDeleted = isDeleted;
        }
    }

    @NoArgsConstructor
    @Getter
    @Schema(description = "관리자 페이지(회원) 고객 정보 조회 Response DTO")
    public static class CustomerDetailResponse {
        @Schema(description = "회원 id")
        private long id;

        @Schema(description = "회원 이름(실명)")
        private String name;

        @Schema(description = "회원 활동 닉네임")
        private String nickName;

        @Schema(description = "이메일")
        private String email;

        @Schema(description = "휴대폰 번호", pattern = "010-xxxx-xxxx")
        private String phoneNumber;

        @Schema(description = "주소(우편번호)", minLength = 5, maxLength = 5)
        private String zipcode;

        @Schema(description = "주소(상세주소)")
        private String address;

        @Schema(description = "프로필 이미지")
        private String profileImage;

        @Schema(description = "회원 역할", defaultValue = "CUSTOMER", pattern = "ex) CUSTOMER(고객), PET_SITTER(돌봄사)")
        private Role role;

        @Schema(description = "소셜 제공자", defaultValue = "NULL", pattern = "ex) KAKAO, NAVER, GOOGLE")
        private SocialProvider socialProvider;

        @Schema(description = "자기소개")
        private String introduction;

        @Schema(description = "보유 적립금")
        private int amount;

        @Schema(description = "보유 반려견")
        private List<PetResponse.GetList> pets = new ArrayList<>();

        @Schema(description = "회원 탈퇴 여부", defaultValue = "false", pattern = "ex) false(활동중), true(탈퇴)")
        private boolean isDeleted;

        public CustomerDetailResponse(Member member, List<Pet> pets) {
            this.id = member.getId();
            this.name = member.getName();
            this.nickName = member.getNickName();
            this.email = member.getEmail();
            this.phoneNumber = member.getPhoneNumber();
            this.zipcode = member.getZipcode();
            this.address = member.getAddress();
            this.profileImage = member.getProfileImage();
            this.role = member.getRole();
            this.socialProvider = member.getSocialProvider();
            this.introduction = (member.getIntroduction() != null) ?
                    member.getIntroduction().replace("\n", "<br>")
                    : "";
            this.amount = member.getAmount();
            this.pets = pets.stream()
                    .map(PetResponse.GetList::new)
                    .collect(Collectors.toList());
            this.isDeleted = member.isDeleted();
        }
    }

    @NoArgsConstructor
    @Getter
    @Schema(description = "관리자 페이지(회원) 돌봄사 정보 조회 Response DTO")
    public static class SitterDetailResponse {
        @Schema(description = "회원 id")
        private long id;

        @Schema(description = "회원 이름(실명)")
        private String name;

        @Schema(description = "회원 활동 닉네임")
        private String nickName;

        @Schema(description = "이메일")
        private String email;

        @Schema(description = "휴대폰 번호", pattern = "010-xxxx-xxxx")
        private String phoneNumber;

        @Schema(description = "주소(우편번호)", minLength = 5, maxLength = 5)
        private String zipcode;

        @Schema(description = "주소(상세주소)")
        private String address;

        @Schema(description = "프로필 이미지")
        private String profileImage;

        @Schema(description = "회원 역할", defaultValue = "CUSTOMER", pattern = "ex) CUSTOMER(고객), PET_SITTER(돌봄사)")
        private Role role;

        @Schema(description = "소셜 제공자", defaultValue = "NULL", pattern = "ex) KAKAO, NAVER, GOOGLE")
        private SocialProvider socialProvider;

        @Schema(description = "자기소개")
        private String introduction;

        @Schema(description = "돌봄사 경력연차", defaultValue = "NULL")
        private Integer careerYear;

        @Schema(description = "보유 자격증")
        private List<CertificationResponse.GetList> certifications;

        @Schema(description = "회원 탈퇴 여부", defaultValue = "false", pattern = "ex) false(활동중), true(탈퇴)")
        private boolean isDeleted;

        public SitterDetailResponse(Member member, List<Certification> certifications) {
            this.id = member.getId();
            this.name = member.getName();
            this.nickName = member.getNickName();
            this.email = member.getEmail();
            this.phoneNumber = member.getPhoneNumber();
            this.zipcode = member.getZipcode();
            this.address = member.getAddress();
            this.profileImage = member.getProfileImage();
            this.role = member.getRole();
            this.socialProvider = member.getSocialProvider();
            this.introduction = (member.getIntroduction() != null) ?
                    member.getIntroduction().replace("\n", "<br>")
                    : "";
            this.careerYear = member.getCareerYear();
            this.certifications = certifications.stream()
                    .map(CertificationResponse.GetList::new)
                    .collect(Collectors.toList());
            this.isDeleted = member.isDeleted();
        }
    }
}
