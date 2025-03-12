package com.PetSitter.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UploadFile {

    @Comment("사용자가 업로드한 파일명")
    private String uploadFileName;

    @Comment("서버 내부에서 관리하는 파일명")
    private String serverFileName;
}
