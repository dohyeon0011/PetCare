package com.PetSitter.service;

import com.PetSitter.domain.UploadFile;
import org.hibernate.annotations.Comment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileUploadService {

    @Value("${profileUpload.path}")
    private String profileUploadPath;

    @Value("${petsUpload.path}")
    private String petsUploadPath;

    @Value("${carelogsUpload.path}")
    private String carelogsUploadPath;

    @Comment("여러 개의 파일 업로드 처리 메서드")
    public List<UploadFile> uploadFiles(List<MultipartFile> multipartFiles, String uploadType) throws IOException {
        if (multipartFiles == null || multipartFiles.isEmpty()) {
            throw new IOException("파일이 비었습니다.");
        }

        List<UploadFile> uploadFiles = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            if (multipartFile == null || multipartFile.isEmpty()) {
                throw new IOException("빈 파일은 업로드할 수 없습니다.");
            }

            String originalFilename = multipartFile.getOriginalFilename();

            if (originalFilename == null || originalFilename.isEmpty()) {
                throw new IOException("파일 이름이 비어있습니다.");
            }

            String serverFileName = createServerFileName(originalFilename);
            multipartFile.transferTo(new File(getFullPath(serverFileName, uploadType)));

            uploadFiles.add(new UploadFile(originalFilename, serverFileName));
        }

        return uploadFiles;
    }

    @Comment("단일 파일 업로드 처리 메서드")
    public UploadFile uploadFile(MultipartFile multipartFile, String uploadType) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new IOException("파일이 비어있습니다.");
        }

        // 파일 이름 가져오기
        String originalFilename = multipartFile.getOriginalFilename();

        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IOException("파일 이름이 비어있습니다.");
        }

        String serverFileName = createServerFileName(originalFilename); // 서버에서 저장할 파일명으로 변환(확장자까지)
        multipartFile.transferTo(new File(getFullPath(serverFileName, uploadType)));

        return new UploadFile(originalFilename, serverFileName);
    }

    @Comment("단일 파일 수정 업로드 처리 메서드")
    public UploadFile updateFile(MultipartFile multipartFile, String uploadType, String currentProfileImage) throws IOException {
        // 기존 파일 삭제
        if (currentProfileImage != null) {
            File currentFile = new File(getFullPath(currentProfileImage, uploadType));

            if (currentFile.exists()) {
                currentFile.delete();
            }
        }

        // 새 파일 업로드
        return uploadFile(multipartFile, uploadType);
    }

    @Comment("파일명 UUID로 바꾸기")
    public String createServerFileName(String originalFilename) throws IOException {
        String uuid = UUID.randomUUID().toString();
        String ext = extractExt(originalFilename); // 확장자만 추출

        String fileName = uuid + "." + ext;

        if (fileName == null || fileName.isEmpty() || !fileName.contains(".")) {
            throw new IOException("파일 이름이 비어있거나 확장자가 없습니다.");
        }

        return fileName;
    }

    @Comment("파일 확장자만 추출")
    public String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf("."); // 확장자 인덱스 찾기

        return originalFilename.substring(pos + 1);
    }

    @Comment("파일 저장 경로 + 이미지 명")
    private String getFullPath(String serverFileName, String uploadType) {
        // 파일 저장 경로 설정
        File uploadDir;
        String uploadPath;

        switch (uploadType) {
            case "profile":
                uploadDir = new File(profileUploadPath);
                uploadPath = profileUploadPath;
                break;
            case "pets":
                uploadDir = new File(petsUploadPath);
                uploadPath = petsUploadPath;
                break;
            case "carelogs":
                uploadDir = new File(carelogsUploadPath);
                uploadPath = carelogsUploadPath;
                break;
            default:
                throw new IllegalArgumentException("지원되지 않는 업로드 타입입니다.");
        }

        if (!uploadDir.exists()) {
            uploadDir.mkdirs(); // 해당 경로에 디렉토리가 없으면 생성
        }

        return uploadPath + serverFileName;
    }
}
