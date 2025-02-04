package com.PetSitter.domain.Member.customer;//package com.PetCare.domain.Member.Customer;
//
//import com.PetCare.domain.Member.Member;
//import com.PetCare.domain.Pet.Pet;
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
//@DiscriminatorValue("CUSTOMER")  // 구분자 역할을 CUSTOMER로 지정
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//public class Customer extends Member {
//
//    @Comment("고객이 보유한 반려견 목록")
//    @JsonIgnore // api 조회시 반려견 목록은 빠지고 조회됨
//    @OneToMany(mappedBy = "customer")
//    private List<Pet> pets = new ArrayList<>();
//
//    // 반려견 추가 메서드
//    public void addPet(Pet... pet) {
//        this.pets.addAll(Arrays.asList(pet));
//    }
//
//}
