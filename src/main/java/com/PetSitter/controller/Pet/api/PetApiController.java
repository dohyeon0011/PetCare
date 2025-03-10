package com.PetSitter.controller.Pet.api;

import com.PetSitter.domain.Member.MemberDetails;
import com.PetSitter.domain.Pet.Pet;
import com.PetSitter.dto.Pet.request.AddPetRequest;
import com.PetSitter.dto.Pet.request.UpdatePetRequest;
import com.PetSitter.dto.Pet.response.PetResponse;
import com.PetSitter.service.Pet.PetService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets-care/members")
public class PetApiController {

    private final PetService petService;

    @Operation(description = "반려견 등록 API")
    @PostMapping("/{customerId}/pets/new")
    public ResponseEntity<List<Pet>> addPet(@PathVariable("customerId") long id, @RequestBody @Valid List<AddPetRequest> requests, @AuthenticationPrincipal MemberDetails memberDetails) {
        if (memberDetails.getMember().getId() != id) {
            return ResponseEntity.badRequest()
                    .build();
        }

        List<Pet> pets = petService.save(id, requests);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pets);
    }

    @Operation(description = "특정 회원의 모든 반려견 조회 API")
    @GetMapping("/{customerId}/pets")
    public ResponseEntity<List<PetResponse.GetList>> findAllPet(@PathVariable("customerId") long id, @AuthenticationPrincipal MemberDetails memberDetails) {
        if (memberDetails.getMember().getId() != id) {
            return ResponseEntity.badRequest()
                    .build();
        }

        List<PetResponse.GetList> pets = petService.findById(id);

        return ResponseEntity.ok()
                .body(pets);
    }

    @Operation(description = "회원의 특정 반려견 삭제 API")
    @DeleteMapping("{customerId}/pets/{petId}")
    public ResponseEntity<Void> deletePet(@PathVariable("customerId") long id, @PathVariable("petId") long petId, @AuthenticationPrincipal MemberDetails memberDetails) {
        if (memberDetails.getMember().getId() != id) {
            return ResponseEntity.badRequest()
                    .build();
        }

        petService.delete(id, petId);

        return ResponseEntity.ok()
                .build();
    }

    @Operation(description = "회원의 반려견 정보 수정 API")
    @PutMapping("{customerId}/pets")
    public ResponseEntity<List<PetResponse.GetList>> updatePet(@PathVariable("customerId") long id, @RequestBody @Valid List<UpdatePetRequest> requests, @AuthenticationPrincipal MemberDetails memberDetails) {
        if (memberDetails.getMember().getId() != id) {
            return ResponseEntity.badRequest()
                    .build();
        }

        List<PetResponse.GetList> updatePets = petService.update(id, requests);

        return ResponseEntity.ok()
                .body(updatePets);
    }

}
