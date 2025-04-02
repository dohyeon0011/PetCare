package com.PetSitter.dto.Member.response;

import com.PetSitter.domain.Certification.Certification;
import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.Role;
import com.PetSitter.domain.Member.SocialProvider;
import com.PetSitter.domain.Pet.Pet;
import com.PetSitter.dto.Certification.response.CertificationResponse;
import com.PetSitter.dto.Pet.response.PetResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class MemberResponse {

    @NoArgsConstructor
    @Getter
    public static class GetCustomer {
        private long id;
        private String name;
        private String nickName;
        private String email;
        private String phoneNumber;
        private String zipcode;
        private String address;
        private String profileImage;
        private Role role;
        private SocialProvider socialProvider;
        private String introduction;
        private Integer careerYear;
        private int amount;
        private List<PetResponse.GetList> pets = new ArrayList<>();

        public GetCustomer(Member member, List<Pet> pets) {
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
            this.amount = member.getAmount();
            this.pets = pets.stream()
                    .map(PetResponse.GetList::new)
                    .collect(Collectors.toList());
        }
    }

    @NoArgsConstructor
    @Getter
    public static class GetSitter {
        private long id;
        private String name;
        private String nickName;
        private String email;
        private String phoneNumber;
        private String zipcode;
        private String address;
        private String profileImage;
        private Role role;
        private SocialProvider socialProvider;
        private String introduction;
        private Integer careerYear;
        private List<CertificationResponse.GetList> certifications;

        public GetSitter(Member member, List<Certification> certifications) {
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
        }
    }

    @NoArgsConstructor
    @Getter
    public static class getUpdateCustomer {
        private long id;
        private String name;
        private String nickName;
        private String email;
        private String phoneNumber;
        private String zipcode;
        private String address;
        private String profileImage;
        private Role role;
        private SocialProvider socialProvider;
        private String introduction;
        private Integer careerYear;
        private int amount;
        private List<PetResponse.GetList> pets = new ArrayList<>();
        private boolean roleChanged; // 회원 역할 변경 여부('CUSTOMER' or 'PET_SITTER')

        public getUpdateCustomer(Member member, List<Pet> pets, boolean roleChanged) {
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
            this.amount = member.getAmount();
            this.pets = pets.stream()
                    .map(PetResponse.GetList::new)
                    .collect(Collectors.toList());
            this.roleChanged = roleChanged;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class getUpdateSitter {
        private long id;
        private String name;
        private String nickName;
        private String email;
        private String phoneNumber;
        private String zipcode;
        private String address;
        private String profileImage;
        private Role role;
        private SocialProvider socialProvider;
        private String introduction;
        private Integer careerYear;
        private List<CertificationResponse.GetList> certifications;
        private boolean roleChanged; // 회원 역할 변경 여부('CUSTOMER' or 'PET_SITTER')

        public getUpdateSitter(Member member, List<Certification> certifications, boolean roleChanged) {
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
            this.roleChanged = roleChanged;
        }
    }
}
