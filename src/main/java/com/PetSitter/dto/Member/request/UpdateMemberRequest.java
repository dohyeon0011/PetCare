package com.PetSitter.dto.Member.request;

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
public class UpdateMemberRequest {

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상, 20자 이하로 입력해야 합니다.")
    private String password;

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(min = 2, max = 8, message = "닉네임은 2자 이상, 8자 이하로 입력해야 합니다.")
    private String nickName;

    @Email(message = "유효한 이메일을 입력해주세요.")
    private String email;

    private String phoneNumber;

    @NotEmpty(message = "우편번호는 필수입니다.")
    @Size(min = 5, max = 5)
    private String zipcode;

    @NotEmpty(message = "상세주소는 필수입니다.")
    private String address;

    private String introduction;

    private Integer careerYear;
}
