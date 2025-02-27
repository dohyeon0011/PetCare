package com.PetSitter.domain.Member;

import com.PetSitter.domain.CareAvailableDate.CareAvailableDate;
import com.PetSitter.domain.Certification.Certification;
import com.PetSitter.domain.Pet.Pet;
import com.PetSitter.domain.Reservation.CustomerReservation.CustomerReservation;
import com.PetSitter.domain.Reservation.SitterSchedule.SitterSchedule;
import com.PetSitter.dto.Member.response.AdminMemberResponse;
import com.PetSitter.dto.Member.response.MemberResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/* 왜 엔티티에서만 @NoArgsConstructor가 필요한지 ??
    JPA는 엔티티를 관리하기 위해 리플렉션과 프록시 객체를 사용하며, 이 과정에서 기본 생성자가 필요함
    따라서 JPA 엔티티 클래스에만 @NoArgsConstructor를 적용하면 된다 (DTO에는 필요 X)
    DTO는 데이터 전송을 목적으로 하는 객체이므로 JPA와 같은 프레임워크가 직접 인스턴스화하지 않음
    DTO는 주로 클라이언트 요청 데이터를 매핑하거나, 엔티티 데이터를 반환하는 용도로 사용됨. 따라서 생성자는 명시적으로 작성하거나,
    Lombok의 @AllArgsConstructor 또는 빌더 패턴을 사용하는 경우가 많음
*/
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자를 생성하되, 외부에서 호출하지 못하도록 접근 제어를 protected로 제한(기본 객체 생성 막기), JPA는 리플렉션을 통해 생성자를 호출하므로 protected 접근제어도 문제없이 동작함
@Entity
@Table(name = "members")
@Getter
@EntityListeners(AuditingEntityListener.class)
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING)
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id", updatable = false)
    private long id;

    // @Column에서 nullable은 데이터베이스 레벨에서 작동(DDL 생성 시 not null 제약 조건 추가, 데이터가 데이터 베이스에 직접 저장될 때 null 값이 들어오면 데이터베이스에서 오류 발생)
    @Comment("사용자가 로그인 할 아이디")
    @Column(nullable = false, unique = true, updatable = false)
    private String loginId;

    @Comment("비밀번호")
    @Column(nullable = false)
    private String password;

    @Comment("사용자 실제 이름")
    @Column(nullable = false)
    private String name;

    @Comment("사용자가 활동할 닉네임")
    @Column(nullable = false, unique = true)
    private String nickName;

    @Email
    private String email;

    private String phoneNumber;

    @Comment("우편번호")
    @Column(length = 5, nullable = false)
    private String zipcode;

    @Comment("상세주소")
    @Column(nullable = false)
    private String address;

    @Comment("회원 역할(고객, 돌봄사, 관리자)")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Comment("프로필 사진")
    private String profileImgPath;

    // 사용자 가입 날짜 확인 : 사용자가 언제 가입했는지 추적할 수 있다
    // 리프레시 토큰 발급 시간 확인 : 리프레시 토큰이 언제 생성되었는지 확인하여 비정상적인 패턴(예: 짧은 시간 내 다중 발급)을 탐지할 수 있다
    // 이벤트 기반 처리 : 가입 후 X일 후 이메일 알림, 혜택 제공 등 타임라인 기반 기능에 사용된다
    @Comment("회원가입 날짜")
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 최근 로그인 시간 확인 : 소셜 로그인 사용자가 로그인할 때마다 정보를 갱신하면, 사용자의 마지막 활동을 추적할 수 있다
    // 리프레시 토큰 갱신 확인 : 리프레시 토큰을 재발급하면 해당 토큰의 updated_at을 갱신하여, 최근 발급 시점을 기록
    // 데이터 무결성 확인 : 예상치 못한 데이터 변경(예: 수동으로 수정된 경우)을 확인하거나, 기록으로 추적할 수 있다
    @Comment("최근 수정 날짜")
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Comment("소셜 로그인 제공자(KAKAO, NAVER, GOOGLE), 기본 값 null")
    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private SocialProvider socialProvider;

    @Comment("사용자 프로필 소개")
    private String introduction;

    @Comment("고객 포인트")
    private int amount;

    @Comment("탈퇴 여부(soft delete), True: 탈퇴, False: 존재")
    private boolean isDeleted;

    // CascadeType.REMOVE만 하면 실 데이터는 삭제된 것처럼 숨어있고, orphanRemoval = true이면 연관관계와 실 데이터까지 모두 삭제 (논리적으로 참조를 변경시켜서 무결성 오류를 안 나게 할 뿐, 데이터는 남게 됨)
    // 지금과 같이 회원 - 반려견, 회원 - 자격증과 같이 부모 자식 관계가 뚜렷한 경우에만 cascade 옵션 쓰고,
    // 학생 - 수강 중인 수업과 같은 하나의 자식에 여러 부모가 있는 경우에는 사용 자제
    // Cascade도 결국엔 해당 테이블의 기본키가 다른 테이블의 외래키로 사용되는 경우 데이터 삭제가 불가능한데 강제로 삭제시키려는 것이니 무결성 오류가 나야하는 것임.
    // 1. Cascade되는 엔티티와 Cascade를 설정하는 엔티티의 라이프사이클이 동일하거나 비슷해야한다.
    // 2. Cascade되는 엔티티가 Cascade를 설정하는 엔티티에서만 사용되어야 한다.
    @Comment("고객이 보유한 반려견 목록")
    @OneToMany(mappedBy = "customer", orphanRemoval = true)
//    @Where(clause = "is_deleted = false") // isDeleted가 false인 데이터만 조회
    @JsonIgnore // api 조회시 반려견 목록은 빠지고 조회됨
    private List<Pet> pets = new ArrayList<>();

    @Comment("고객이 예약한 예약 목록")
    @OneToMany(mappedBy = "customer", orphanRemoval = true)
    @JsonIgnore
    private List<CustomerReservation> customerReservations = new ArrayList<>();

    // ----------- 여기까지 고객 필드 -----------

    @Comment("돌봄사 경력 연차")
    private Integer careerYear;

    @Comment("돌봄사가 보유한 자격증")
    @OneToMany(mappedBy = "sitter", orphanRemoval = true)
    @JsonIgnore
    private List<Certification> certifications = new ArrayList<>();

    @Comment("돌봄사가 돌봄 가능한 시간")
    @OneToMany(mappedBy = "sitter", orphanRemoval = true)
    @JsonIgnore
    private List<CareAvailableDate> careAvailabilities = new ArrayList<>();

    @Comment("돌봄사의 예약된 목록")
    @OneToMany(mappedBy = "sitter", orphanRemoval = true)
    @JsonIgnore
    private List<SitterSchedule> sitterSchedules = new ArrayList<>();

    // ----------- 여기는 돌봄사 필드 -----------

    //    @Comment("돌봄사가 보유한 자격증")
//    @Convert(converter = CertificateListConverter.class)
//    @Column(columnDefinition = "TEXT") // 필요 시 길이를 늘림
//    private List<String> certifications;

    @Builder
    public Member(String loginId, String password, String name, String nickName, String email, String phoneNumber, String zipcode, String address, Role role, SocialProvider socialProvider, String introduction, Integer careerYear) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.nickName = nickName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.zipcode = zipcode;
        this.address = address;
        this.role = role;
//        this.profileImgPath = profileImgPath;
        this.socialProvider = socialProvider;
        this.introduction = introduction;
        this.careerYear = careerYear;
//        this.certifications.get().addPetSitter(this);
    }

    @Comment("회원정보 수정")
    public void update(String password, String name, String nickName, String email, String phoneNumber, String zipcode, String address, String profileImgPath, String introduction, Integer careerYear) {
        this.password = password;
        this.name = name;
        this.nickName = nickName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.zipcode = zipcode;
        this.address = address;
//        this.role = Role.valueOf(role);
        this.profileImgPath = profileImgPath;
        this.introduction = introduction;

        if (Role.PET_SITTER.equals(this.getRole())) {
            this.careerYear = careerYear;
        }
    }

    @Comment("적립금 추가")
    public void addRewardPoints(int amount) {
        this.amount += amount;
    }

    @Comment("적립금 차감")
    public void subRewardPoints(int amount) {
        this.amount -= amount;
    }

    @Comment("회원 탈퇴 시 Soft Delete 적용")
    public void changeIsDeleted(boolean isDeleted) {
        if (this.isDeleted) {
            throw new IllegalArgumentException("이미 탈퇴한 사용자입니다.");
        }

        this.isDeleted = isDeleted;
    }

    // 이러한 상황(Member의 Role)에 따른 로직은 도메인 내부에 있어야 변경사항이 있을 때 도메인만 수정하면 돼서 유지보수가 쉽다.
    // 이런 로직 같은 경우는 서비스, 컨트롤러 레벨에 작성해도 되지만 컨트롤러 레벨에서는 요청 전달 및 응답 반환만 해야 하는 것이 정석이고,
    // 서비스 레벨에서는 데이터 조회 및 비즈니스 로직 처리 요청만을 하는 것이 좋다.
    public Object toResponse() {
        if (Role.CUSTOMER.equals(this.getRole())) {
            return new MemberResponse.GetCustomer(this, this.pets);
        } else if (Role.PET_SITTER.equals(this.getRole())) {
            return new MemberResponse.GetSitter(this, this.certifications);
        }
        throw new NoSuchElementException("존재하지 않는 회원입니다.");
    }

    @Comment("관리자 페이지 회원 상세 정보 조회")
    public Object toDetailResponseForAdmin() {
        if (Role.CUSTOMER.equals(this.getRole())) {
            return new AdminMemberResponse.CustomerDetailResponse(this, this.pets);
        } else if (Role.PET_SITTER.equals(this.getRole())) {
            return new AdminMemberResponse.SitterDetailResponse(this, this.certifications);
        }
        throw new NoSuchElementException("존재하지 않는 회원입니다.");
    }
}