package com.PetSitter.service.CareLog;

import com.PetSitter.domain.CareLog.CareLog;
import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.Role;
import com.PetSitter.domain.Reservation.CustomerReservation.ReservationStatus;
import com.PetSitter.domain.Reservation.SitterSchedule.SitterSchedule;
import com.PetSitter.domain.UploadFile;
import com.PetSitter.dto.CareLog.request.AddCareLogRequest;
import com.PetSitter.dto.CareLog.request.UpdateCareLogRequest;
import com.PetSitter.dto.CareLog.response.CareLogResponse;
import com.PetSitter.repository.CareLog.CareLogRepository;
import com.PetSitter.repository.Member.MemberRepository;
import com.PetSitter.repository.Reservation.SitterSchedule.SitterScheduleRepository;
import com.PetSitter.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.hibernate.annotations.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class CareLogService {

    private final MemberRepository memberRepository;
    private final SitterScheduleRepository sitterScheduleRepository;
    private final CareLogRepository careLogRepository;
    private final FileUploadService fileUploadService;

    @Comment("케어 로그 작성")
    public CareLogResponse.GetDetail save(long sitterId, long sitterScheduleId, AddCareLogRequest request, MultipartFile uploadFile) throws FileUploadException {
        Member sitter = memberRepository.findById(sitterId)
                .orElseThrow(() -> new NoSuchElementException("로그인한 회원을 찾을 수 없습니다."));

        authorizationMember(sitter);
        verifyingPermissionsCustomer(sitter);

        SitterSchedule sitterSchedule = sitterScheduleRepository.findById(sitterScheduleId)
                .orElseThrow(() -> new NoSuchElementException("해당 돌봄 예약이 존재하지 않습니다."));

        if (sitterSchedule.getStatus().equals(ReservationStatus.CANCEL)) {
            throw new IllegalArgumentException("취소된 예약에는 케어 로그 작성이 불가능합니다.");
        }

        UploadFile careLogImage = null;

        if (uploadFile != null && !uploadFile.isEmpty()) {
            try {
                careLogImage = fileUploadService.uploadFile(uploadFile, "carelogs");
            } catch (IOException e) {
                throw new FileUploadException("케어 로그 이미지 파일 업로드 중에 실패했습니다.", e);
            }
        }

        CareLog careLog = request.toEntity(sitterSchedule, careLogImage != null ? careLogImage.getServerFileName() : null);
        careLogRepository.save(careLog);

//        return careLog.toResponse();

        return careLogRepository.findCareLogDetail(careLog.getId(), sitterId)
                .orElseThrow(() -> new NoSuchElementException("해당 돌봄 케어 로그를 불러오는데 실패했습니다."));
    }

    @Comment("돌봄사가 작성한 모든 돌봄 케어 로그 조회")
    @Transactional(readOnly = true)
    public Page<CareLogResponse.GetList> findAll(long sitterId, Pageable pageable) {
        /*List<CareLog> careLogList = careLogRepository.findAllBySitterScheduleSitterId(sitterId);

        return careLogList.stream()
                .map(CareLogResponse.GetList::new)
                .toList();*/

        Page<CareLogResponse.GetList> careLogList = careLogRepository.findAllBySitterId(sitterId, pageable);

        return careLogList;
    }

    @Comment("돌봄사가 특정 돌봄에 대해 작성한 돌봄 케어 로그 전체 조회")
    @Transactional(readOnly = true)
    public List<CareLogResponse.GetDetail> findAllById(long sitterId, long sitterScheduleId) {
        /*List<CareLog> careLogList = careLogRepository.findAllBySitterScheduleSitterIdAndSitterScheduleId(sitterId, sitterScheduleId);

        if (careLogList.isEmpty()) {
            throw new NoSuchElementException("해당 돌봄에 대해 작성된 케어 로그가 존재하지 않습니다.");
        }

        return careLogList
                .stream()
                .map(CareLogResponse.GetDetail::new)
                .toList();*/

        List<CareLogResponse.GetDetail> careLogList = careLogRepository.findAllCareLogDetail(sitterId, sitterScheduleId);

        if (careLogList.isEmpty()) {
            throw new NoSuchElementException("해당 돌봄에 대해 작성된 케어 로그가 존재하지 않습니다.");
        }
        return careLogList;
    }

    @Comment("돌봄사가 특정 돌봄에 대해 작성한 특정 돌봄 케어 로그 단건 조회")
    @Transactional(readOnly = true)
    public CareLogResponse.GetDetail findById(long sitterId, long careLogId) {
        /*CareLog careLog = careLogRepository.findBySitterScheduleSitterIdAndId(sitterId, careLogId)
                .orElseThrow(() -> new NoSuchElementException("해당 돌봄 케어 로그 조회에 실패했습니다."));

        return careLog.toResponse();*/

        CareLogResponse.GetDetail careLog = careLogRepository.findCareLogDetail(careLogId, sitterId)
                .orElseThrow(() -> new NoSuchElementException("해당 돌봄 케어 로그 조회에 실패했습니다."));

        return careLog;
    }

    @Comment("돌봄사의 특정 돌봄 케어 로그 삭제")
    public void delete(long sitterId, long careLogId) {
        Member sitter = memberRepository.findById(sitterId)
                .orElseThrow(() -> new NoSuchElementException("로그인한 회원을 찾을 수 없습니다."));

        verifyingPermissionsCustomer(sitter);
        authorizationMember(sitter);

        CareLog careLog = careLogRepository.findBySitterScheduleSitterIdAndIdAndIsDeletedFalse(sitter.getId(), careLogId)
                .orElseThrow(() -> new NoSuchElementException("해당 돌봄 케어 로그가 존재하지 않습니다."));

//        careLogRepository.delete(careLog);
        careLog.changeIsDeleted(true);
    }

    @Comment("돌봄사의 특정 돌봄 케어 로그 수정")
    public CareLogResponse.GetDetail update(long sitterId, long careLogId, UpdateCareLogRequest request, MultipartFile uploadFile) throws FileUploadException {
        Member sitter = memberRepository.findById(sitterId)
                .orElseThrow(() -> new NoSuchElementException("로그인한 회원을 찾을 수 없습니다."));

        verifyingPermissionsCustomer(sitter);
        authorizationMember(sitter);

        CareLog careLog = careLogRepository.findBySitterScheduleSitterIdAndIdAndIsDeletedFalse(sitter.getId(), careLogId)
                .orElseThrow(() -> new NoSuchElementException("해당 돌봄 케어 로그가 존재하지 않습니다."));

        UploadFile updateCareLogImage = null;

        if (uploadFile != null && !uploadFile.isEmpty()) {
            try {
                updateCareLogImage = fileUploadService.updateFile(uploadFile, "carelogs", careLog.getImage());
            } catch (IOException e) {
                throw new FileUploadException("케어 로그 이미지 파일 업로드 중에 실패했습니다.", e);
            }
        }

        careLog.updateCareLog(request.getCareType(), request.getDescription(), updateCareLogImage != null ? updateCareLogImage.getServerFileName() : null);

        return careLogRepository.findCareLogDetail(careLog.getId(), sitterId)
                .orElseThrow(() -> new NoSuchElementException("해당 돌봄 케어 로그가 존재하지 않습니다."));
    }

    @Comment("돌봄 케어 로그 작성할 때 보여줄 정보")
    @Transactional(readOnly = true)
    public CareLogResponse.GetNewCareLog getReservation(long sitterScheduleId) {
        /*SitterSchedule sitterSchedule = sitterScheduleRepository.findById(sitterScheduleId)
                .orElseThrow(() -> new NoSuchElementException("해당 돌봄 예약을 조회하는데 실패했습니다."));

        return new CareLogResponse.GetNewCareLog(sitterSchedule);*/

        CareLogResponse.GetNewCareLog response = sitterScheduleRepository.findBySitterScheduleId(sitterScheduleId)
                .orElseThrow(() -> new NoSuchElementException("해당 돌봄 예약을 조회하는데 실패했습니다."));

        return response;
    }

    private static void authorizationMember(Member member) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName(); // 로그인에 사용된 아이디 값 반환

        if(!member.getLoginId().equals(userName)) {
            throw new IllegalArgumentException("회원 본인만 가능합니다.");
        }
    }

    public static void verifyingPermissionsCustomer(Member sitter) {
        if (!sitter.getRole().equals(Role.PET_SITTER)) {
            throw new IllegalArgumentException("돌봄사만 돌봄 케어 로그 작성,수정 및 삭제가 가능합니다.");
        }
    }
}
