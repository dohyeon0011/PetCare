package com.PetSitter.controller.Pet.api;

import com.PetSitter.config.exception.BadRequestCustom;
import com.PetSitter.domain.Member.MemberDetails;
import com.PetSitter.domain.Pet.Pet;
import com.PetSitter.domain.UploadFile;
import com.PetSitter.dto.Pet.request.AddPetRequest;
import com.PetSitter.dto.Pet.request.AddPetRequests;
import com.PetSitter.dto.Pet.request.UpdatePetRequest;
import com.PetSitter.dto.Pet.request.UpdatePetRequests;
import com.PetSitter.dto.Pet.response.PetResponse;
import com.PetSitter.service.FileUploadService;
import com.PetSitter.service.Pet.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets-care/members")
@Slf4j
public class PetApiController {

    private final PetService petService;

    @Operation(summary = "고객 - 반려견 등록", description = "반려견 등록 API")
    @PostMapping(value = "/{customerId}/pets/new", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addPet(@PathVariable("customerId") @Parameter(required = true, description = "회원(고객) 고유 번호") long id,
                                            @ModelAttribute(value = "request") @Parameter(required = true, description = "등록할 반려견 정보") @Valid AddPetRequests requests,
                                            @AuthenticationPrincipal MemberDetails memberDetails) throws FileUploadException {
        if (memberDetails.getMember().getId() != id) {
            throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
        }

        List<Pet> pets = petService.save(id, requests.getRequests());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pets);
    }

    @Operation(summary = "고객 - 모든 반려견 조회", description = "특정 고객의 모든 반려견 조회 API")
    @GetMapping("/{customerId}/pets")
    public ResponseEntity<List<PetResponse.GetList>> findAllPet(@PathVariable("customerId") @Parameter(required = true, description = "회원(고객) 고유 번호") long id, @AuthenticationPrincipal MemberDetails memberDetails) {
        if (memberDetails != null && memberDetails.getMember().getId() != id) {
            throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
        }

        List<PetResponse.GetList> pets = petService.findById(id);

        return ResponseEntity.ok()
                .body(pets);
    }

    @Operation(summary = "고객 - 특정 반려견 삭제", description = "특정 반려견 삭제 API")
    @DeleteMapping("{customerId}/pets/{petId}")
    public ResponseEntity<Void> deletePet(@PathVariable("customerId") @Parameter(required = true, description = "회원(고객) 고유 번호") long id, @PathVariable("petId") long petId, @AuthenticationPrincipal MemberDetails memberDetails) {
        if (memberDetails != null && memberDetails.getMember().getId() != id) {
            throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
        }

        petService.delete(id, petId);

        return ResponseEntity.ok()
                .build();
    }

    @Operation(summary = "고객 - 반려견 정보 수정", description = "반려견 정보 수정 API")
    @PutMapping(value = "{customerId}/pets", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<PetResponse.GetList>> updatePet(@PathVariable("customerId") @Parameter(required = true, description = "회원(고객) 고유 번호") long id,
                                                               @ModelAttribute(value = "request") @Parameter(required = true, description = "수정할 반려견의 정보") @Valid UpdatePetRequests requests,
                                                               @AuthenticationPrincipal MemberDetails memberDetails) throws FileUploadException {
        if (memberDetails != null && memberDetails.getMember().getId() != id) {
            throw new BadRequestCustom("잘못된 요청입니다. 유효한 ID가 아닙니다.");
        }

        List<PetResponse.GetList> updatePets = petService.update(id, requests.getUpdatePetRequests());

        return ResponseEntity.ok()
                .body(updatePets);
    }

}
