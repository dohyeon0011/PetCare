package com.PetSitter.dto.Member.request;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.Role;
import com.PetSitter.domain.Member.SocialProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/* @NoArgsConstructor -> DTO에서 필요한가?
    DTO가 JSON 요청 데이터를 매핑할 때 Jackson이나 다른 직렬화/역직렬화 라이브러리가 기본 생성자가 필요
    예를 들어, 컨트롤러에서 요청 본문으로 들어온 JSON 데이터를 @RequestBody로 매핑할 경우 @NoArgsConstructor가 필요.
 */
@NoArgsConstructor
@AllArgsConstructor // 테스트 코드용 -> 임시로 빠르게 데이터를 생성해내려고 하기 위해 쓰임, Jackson이나 MapStruck 같은 매핑 라이브러리가 DTO 객체를 다룰 때 필요한 기본 생성자를 제외하고도 필드를 매핑하는 데 유리, Spring의 @ModelAttribute나 @RequestBody를 사용할 때도 유용.
@Getter
@Builder // 테스트 코드에서 쓰려고
@Schema(description = "회원 가입 Req DTO")
public class AddMemberRequest {

    @Schema(description = "로그인 할 id", minLength = 5, maxLength = 15)
    @NotBlank(message = "아이디는 필수입니다.") // 애플리케이션 레벨에서 작동(데이터를 데이터베이스에 저장하기 전 검사, 유효성 통과 못 하면 예외 터짐)
    @Size(min = 5, max = 15, message = "아이디는 5자 이상, 15자 이하로 입력해야 합니다.")
    private String loginId;

    @Schema(description = "비밀번호", minLength = 8, maxLength = 20)
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상, 20자 이하로 입력해야 합니다.")
    private String password;

    @Schema(description = "이름(실명)")
    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @Schema(description = "활동 닉네임", minLength = 2, maxLength = 8)
    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(min = 2, max = 8, message = "닉네임은 2자 이상, 8자 이하로 입력해야 합니다.")
    private String nickName;

    @Schema(description = "이메일")
    @Email(message = "유효한 이메일을 입력해주세요.")
    private String email;

    @Schema(description = "휴대폰 번호", pattern = "010-xxxx-xxxx")
    private String phoneNumber;

    @Schema(description = "주소(우편번호)", minLength = 5, maxLength = 5)
    @NotEmpty(message = "우편번호는 필수입니다.")
    @Size(min = 5, max = 5)
    private String zipcode;

    @Schema(description = "주소(상세주소)")
    @NotEmpty(message = "상세주소는 필수입니다.")
    private String address;

    private String profileImg;

    @Schema(description = "회원 역할", defaultValue = "CUSTOMER", pattern = "CUSTOMER(고객), PET_SITTER(돌봄사)")
    @NotNull
    private String role; // DTO에서는 문자열로 Role 값 받고 난 뒤에 enum 타입으로 변환

    @Schema(description = "소셜 제공자", defaultValue = "NULL", pattern = "KAKAO, NAVER, GOOGLE")
    private String socialProvider;

    @Schema(description = "자기소개")
    @Lob
    private String introduction;

    @Schema(description = "돌봄사 경력연차", defaultValue = "NULL")
    private Integer careerYear;

    // DTO에서 엔티티 객체로 변환하는 메서드
    public Member toEntity(String encodedPassword, String profileImage) {
        // socialProvider가 null이면 기본값을 설정
        SocialProvider provider = (socialProvider != null) ? SocialProvider.valueOf(socialProvider) : SocialProvider.NONE;

        return Member.builder()
                .loginId(loginId)
                .password(encodedPassword)
                .name(name)
                .nickName(nickName)
                .email(email)
                .phoneNumber(phoneNumber)
                .zipcode(zipcode)
                .address(address)
                .profileImage(profileImage)
                .role(Role.valueOf(role))  // Role을 Enum으로 변환
                .socialProvider(provider)  // SocialProvider 처리
                .introduction(introduction)
                .careerYear(Role.valueOf(role).equals(Role.CUSTOMER) ? null : careerYear)
                .build();
    }
}