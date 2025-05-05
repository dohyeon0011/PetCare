package com.PetSitter.dto.Member.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Schema(description = "회원 정보 수정 Req DTO")
public class UpdateMemberRequest {

    @Schema(description = "비밀번호", minLength = 8, maxLength = 20)
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

    @NotEmpty(message = "우편번호는 필수입니다.")
    @Size(min = 5, max = 5)
    private String zipcode;

    @NotEmpty(message = "상세주소는 필수입니다.")
    private String address;

    @Schema(description = "자기소개")
    private String introduction;

    @Schema(description = "돌봄사 경력연차", defaultValue = "NULL")
    private Integer careerYear;

    @Schema(description = "회원 역할", defaultValue = "CUSTOMER", pattern = "CUSTOMER(고객), PET_SITTER(돌봄사)")
    private String role;
}
