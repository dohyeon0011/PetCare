package com.PetSitter;

import com.PetSitter.domain.CareAvailableDate.CareAvailableDate;
import com.PetSitter.domain.Certification.Certification;
import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.Role;
import com.PetSitter.domain.Member.SocialProvider;
import com.PetSitter.domain.Pet.Pet;
import com.PetSitter.domain.Reservation.CustomerReservation.CustomerReservation;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class InitDB {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit1();
        initService.dbInit2();
        initService.dbInit3();
        initService.dbInit4();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final EntityManager em;

        public void dbInit1() {
            Member member = Member.builder()
                    .loginId("user1")
                    .password("aaw131aaw131")
                    .name("구창모")
                    .nickName("창모")
                    .email("changmo@naver.com")
                    .phoneNumber("010-1234-5678")
                    .zipcode("12345")
                    .address("경기도")
                    .role(Role.valueOf("CUSTOMER"))
                    .socialProvider(null)
                    .introduction("귀여운 아이들 주인입니다.")
                    .careerYear(null)
                    .build();

            em.persist(member);

            Pet pet1 = Pet.builder()
                    .name("휴지")
                    .age(5)
                    .breed("비숑")
                    .medicalConditions("특정 사료만 먹음")
                    .profileImgPath("https://cafe24.poxo.com/ec01/ejvpt/HOvhRhvOk+Cp2KY4JuusAg5T53na+Q88SEWskSb9Ko/8BAzWksUIq5Cxa0iMqHXD5IlcFun8qoZd+P4AUmvlyQ==/_/web/product/medium/202407/367bd9188f2f35d58c2c8be59c6196d5.jpg")
                    .build();

            Pet pet2 = Pet.builder()
                    .name("초코")
                    .age(2)
                    .breed("포메라니안")
                    .medicalConditions("간식 주면 안됨")
                    .profileImgPath("https://cdn.crowdpic.net/detail-thumb/thumb_d_C1A78936BB1B43554DE572091820B23F.jpg")
                    .build();

            pet1.addCustomer(member);
            pet2.addCustomer(member);

            em.persist(pet1);
            em.persist(pet2);
        }

        public void dbInit2() {
            Member member = Member.builder()
                    .loginId("user2")
                    .password("blackrose12")
                    .name("윤진영")
                    .nickName("애쉬아일랜드")
                    .email("ashisland@naver.com")
                    .phoneNumber("010-1234-5678")
                    .zipcode("22222")
                    .address("서울")
                    .role(Role.valueOf("PET_SITTER"))
                    .socialProvider(SocialProvider.valueOf("KAKAO"))
                    .introduction("섬세한 돌봄사 입니다.")
                    .careerYear(5)
                    .build();

            em.persist(member);

            Certification certification1 = Certification.builder()
                    .name("돌봄1급")
                    .build();

            Certification certification2 = Certification.builder()
                    .name("돌봄2급")
                    .build();

            CareAvailableDate careAvailableDate1 = CareAvailableDate.builder()
                    .availableAt(LocalDate.parse("1999-01-31"))
                    .price(70000)
                    .build();

            CareAvailableDate careAvailableDate2 = CareAvailableDate.builder()
                    .availableAt(LocalDate.parse("1980-05-12"))
                    .price(50000)
                    .build();

            CareAvailableDate careAvailableDate3 = CareAvailableDate.builder()
                    .availableAt(LocalDate.parse("2024-05-12"))
                    .price(50000)
                    .build();

            CareAvailableDate careAvailableDate4 = CareAvailableDate.builder()
                    .availableAt(LocalDate.parse("2024-05-31"))
                    .price(25000)
                    .build();

            CareAvailableDate careAvailableDate5 = CareAvailableDate.builder()
                    .availableAt(LocalDate.parse("2024-04-30"))
                    .price(35000)
                    .build();

            CareAvailableDate careAvailableDate6 = CareAvailableDate.builder()
                    .availableAt(LocalDate.parse("2024-09-02"))
                    .price(45000)
                    .build();

            CareAvailableDate careAvailableDate7 = CareAvailableDate.builder()
                    .availableAt(LocalDate.parse("2024-10-21"))
                    .price(55000)
                    .build();

            CareAvailableDate careAvailableDate8 = CareAvailableDate.builder()
                    .availableAt(LocalDate.parse("2024-10-24"))
                    .price(65000)
                    .build();

            CareAvailableDate careAvailableDate9 = CareAvailableDate.builder()
                    .availableAt(LocalDate.parse("2024-12-12"))
                    .price(70000)
                    .build();

            certification1.addSitter(member);
            certification2.addSitter(member);

            careAvailableDate1.addPetSitter(member);
            careAvailableDate2.addPetSitter(member);
            careAvailableDate3.addPetSitter(member);
            careAvailableDate4.addPetSitter(member);
            careAvailableDate5.addPetSitter(member);
            careAvailableDate6.addPetSitter(member);
            careAvailableDate7.addPetSitter(member);
            careAvailableDate8.addPetSitter(member);
            careAvailableDate9.addPetSitter(member);

            em.persist(certification1);
            em.persist(certification2);

            em.persist(careAvailableDate1);
            em.persist(careAvailableDate2);
            em.persist(careAvailableDate3);
            em.persist(careAvailableDate4);
            em.persist(careAvailableDate5);
            em.persist(careAvailableDate6);
            em.persist(careAvailableDate7);
            em.persist(careAvailableDate8);
            em.persist(careAvailableDate9);
        }

        public void dbInit3() {
            Member member = Member.builder()
                    .loginId("user3")
                    .password("aaw131aaw131")
                    .name("김훈기")
                    .nickName("훈기")
                    .email("originalgimchi@naver.com")
                    .phoneNumber("010-1234-5678")
                    .zipcode("12345")
                    .address("경기도")
                    .role(Role.valueOf("CUSTOMER"))
                    .socialProvider(null)
                    .introduction(null)
                    .careerYear(null)
                    .build();

            em.persist(member);

            Pet pet1 = Pet.builder()
                    .name("삐약")
                    .age(5)
                    .breed("골든 리트리버")
                    .medicalConditions("멍청함")
                    .profileImgPath("https://cafe24.poxo.com/ec01/ejvpt/HOvhRhvOk+Cp2KY4JuusAg5T53na+Q88SEWskSb9Ko/8BAzWksUIq5Cxa0iMqHXD5IlcFun8qoZd+P4AUmvlyQ==/_/web/product/medium/202407/367bd9188f2f35d58c2c8be59c6196d5.jpg")
                    .build();

            Pet pet2 = Pet.builder()
                    .name("토르")
                    .age(2)
                    .breed("포메라니안")
                    .medicalConditions("산책에 미쳐있음")
                    .profileImgPath("https://cdn.crowdpic.net/detail-thumb/thumb_d_C1A78936BB1B43554DE572091820B23F.jpg")
                    .build();

            pet1.addCustomer(member);
            pet2.addCustomer(member);

            em.persist(pet1);
            em.persist(pet2);
        }

        public void dbInit4() {
            Member member = Member.builder()
                    .loginId("user4")
                    .password("akffhs123!")
                    .name("Post Malone")
                    .nickName("포스트 말론")
                    .email("postmalone@gmail.com")
                    .phoneNumber("010-1234-5678")
                    .zipcode("22222")
                    .address("서울")
                    .role(Role.valueOf("PET_SITTER"))
                    .socialProvider(SocialProvider.valueOf("NAVER"))
                    .introduction("POP STAR")
                    .careerYear(2)
                    .build();

            em.persist(member);

            Certification certification1 = Certification.builder()
                    .name("돌봄1급")
                    .build();

            Certification certification2 = Certification.builder()
                    .name("돌봄2급")
                    .build();

            CareAvailableDate careAvailableDate1 = CareAvailableDate.builder()
                    .availableAt(LocalDate.parse("2024-12-31"))
                    .price(30000)
                    .build();

            CareAvailableDate careAvailableDate2 = CareAvailableDate.builder()
                    .availableAt(LocalDate.parse("2023-12-31"))
                    .price(50000)
                    .build();

            CareAvailableDate careAvailableDate3 = CareAvailableDate.builder()
                    .availableAt(LocalDate.parse("2025-01-05"))
                    .price(50000)
                    .build();

            CareAvailableDate careAvailableDate4 = CareAvailableDate.builder()
                    .availableAt(LocalDate.parse("2024-01-14"))
                    .price(50000)
                    .build();

            CareAvailableDate careAvailableDate5 = CareAvailableDate.builder()
                    .availableAt(LocalDate.parse("2024-12-11"))
                    .price(30000)
                    .build();

            CareAvailableDate careAvailableDate6 = CareAvailableDate.builder()
                    .availableAt(LocalDate.parse("2024-08-07"))
                    .price(35000)
                    .build();

            CareAvailableDate careAvailableDate7 = CareAvailableDate.builder()
                    .availableAt(LocalDate.parse("2024-07-03"))
                    .price(40000)
                    .build();

            CareAvailableDate careAvailableDate8 = CareAvailableDate.builder()
                    .availableAt(LocalDate.parse("2024-03-23"))
                    .price(45000)
                    .build();

            CareAvailableDate careAvailableDate9 = CareAvailableDate.builder()
                    .availableAt(LocalDate.parse("2024-11-03"))
                    .price(75000)
                    .build();

            CareAvailableDate careAvailableDate10 = CareAvailableDate.builder()
                    .availableAt(LocalDate.parse("2024-08-08"))
                    .price(80000)
                    .build();

            certification1.addSitter(member);
            certification2.addSitter(member);

            careAvailableDate1.addPetSitter(member);
            careAvailableDate2.addPetSitter(member);
            careAvailableDate3.addPetSitter(member);
            careAvailableDate4.addPetSitter(member);
            careAvailableDate5.addPetSitter(member);
            careAvailableDate6.addPetSitter(member);
            careAvailableDate7.addPetSitter(member);
            careAvailableDate8.addPetSitter(member);
            careAvailableDate9.addPetSitter(member);
            careAvailableDate10.addPetSitter(member);
            careAvailableDate2.reservation();

            em.persist(certification1);
            em.persist(certification2);

            em.persist(careAvailableDate1);
            em.persist(careAvailableDate2);
            em.persist(careAvailableDate3);
            em.persist(careAvailableDate4);
            em.persist(careAvailableDate5);
            em.persist(careAvailableDate6);
            em.persist(careAvailableDate7);
            em.persist(careAvailableDate8);
            em.persist(careAvailableDate9);
            em.persist(careAvailableDate10);
        }
    }
}
