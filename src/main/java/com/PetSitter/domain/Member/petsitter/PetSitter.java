package com.PetSitter.domain.Member.petsitter;//package com.PetCare.domain.Member.PetSitter;
//
//
//import com.PetCare.domain.Member.Member;
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import jakarta.persistence.DiscriminatorValue;
//import jakarta.persistence.Entity;
//import jakarta.persistence.OneToMany;
//import lombok.AccessLevel;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import org.hibernate.annotations.Comment;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//@Entity
//@Getter
//@DiscriminatorValue("PET_SITTER")  // 구분자 역할을 PET_SITTER로 지정
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//public class PetSitter extends Member {
//
//    @Comment("돌봄사 경력 연차")
//    private int careerYear;
//
//    @Comment("돌봄사가 보유한 자격증 목록")
//    @JsonIgnore // api 조회시 반려견 목록은 빠지고 조회됨
//    @OneToMany(mappedBy = "certification_id")
//    private List<Certification> certifications = new ArrayList<>();
//
//    // 자격증 추가 메서드
//    public void addCertification(Certification... certification) {
//        this.certifications.addAll(Arrays.asList(certification));
//    }
//
//    // 생성자
//
//
//}
