package com.PetSitter.service.Pet;

import com.PetSitter.config.annotation.ReadOnlyTransactional;
import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.Role;
import com.PetSitter.domain.Pet.Pet;
import com.PetSitter.domain.UploadFile;
import com.PetSitter.dto.Pet.request.AddPetRequest;
import com.PetSitter.dto.Pet.request.UpdatePetRequest;
import com.PetSitter.dto.Pet.response.PetResponse;
import com.PetSitter.repository.Member.MemberRepository;
import com.PetSitter.repository.Pet.PetRepository;
import com.PetSitter.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class PetService {

    private final PetRepository petRepository;
    private final FileUploadService fileUploadService;
    private final MemberRepository memberRepository;

    @Transactional
    public List<Pet> save(long customerId, List<AddPetRequest> requests) throws FileUploadException {
        if (requests.isEmpty()) {
            throw new IllegalArgumentException("반려견 등록 요청 데이터가 잘못 되었습니다.");
        }
        Member customer = memberRepository.findById(customerId)
                .orElseThrow(() -> new NoSuchElementException("반려견 등록 오류: 현재 회원은 존재하지 않는 회원입니다."));
        verifyingPermissions(customer);

        List<Pet> pets = new ArrayList<>();
        for (AddPetRequest request : requests) {
            UploadFile uploadFile = null;

            if (request.getProfileImage() != null && !request.getProfileImage().isEmpty()) {
                try {
                    uploadFile = fileUploadService.uploadFile(request.getProfileImage(), "pets");
                } catch (IOException e) {
                    throw new FileUploadException("반려견 프로필 이미지 업로드에 실패했습니다.", e);
                }
            }
            Pet pet = request.toEntity(uploadFile != null ? uploadFile.getServerFileName() : null);
            pet.addCustomer(customer);
            pets.add(pet);
        }
        return petRepository.saveAll(pets);
    }

    /**
     * 특정 회원의 반려견 조회
     * @ReadOnlyTransactional: 커스텀 읽기 전용 어노테이션
     */
    @ReadOnlyTransactional
    public List<PetResponse.GetList> findById(long customerId) {
        return petRepository.findByCustomerIdAndIsDeletedFalse(customerId)
                .stream()
                .map(PetResponse.GetList::new)
                .toList();
    }

    /**
     * 특정 회원의 특정 반려견 삭제
     */
    public void delete(long customerId, long petId) {
        Member customer = memberRepository.findById(customerId)
                .orElseThrow(() -> new NoSuchElementException("회원 정보를 불러오는데 실패했습니다."));
        verifyingPermissions(customer);
        authorizationMember(customer);

        Pet findPet = petRepository.findByCustomerIdAndId(customer.getId(), petId)
                .orElseThrow(() -> new NoSuchElementException("등록한 반려견이 존재하지 않습니다."));
        findPet.changeIsDeleted(true);
        petRepository.saveAndFlush(findPet); // 단건 update는 @Transactional 제거 후 직접 db에 flush가 비용이 쌈. 대신, 영속성 컨텍스트 관리 비용이 들어감.(jpql or native query로도 가능.)

        // 여러 개의 엔티티를 한 번의 쿼리로 삭제하는 방식으로 성능을 개선
        // 이 방식은 영속성 컨텍스트(Persistence Context)에서 해당 엔티티들을 관리하지 않으므로, 엔티티 상태가 영속성 컨텍스트와 동기화되지 않음
        // 즉, 삭제 후 해당 엔티티는 더 이상 영속성 컨텍스트에서 관리되지 않으며, 이후에 해당 엔티티에 접근하려면 다시 조회해야 함
//        petRepository.delete(findPet);
//        petRepository.deleteAllInBatch(pets);
//        petRepository.deleteAll(pets); // 얘는 여러번 쿼리 나감
    }

    /**
     * 특정 회원의 반려견 수정
     */
    @Transactional
    public List<PetResponse.GetList> update(long customerId, List<UpdatePetRequest> requests) throws FileUploadException {
        if (requests.isEmpty()) {
            throw new IllegalArgumentException("반려견 등록 요청 데이터가 잘못 되었습니다.");
        }
        Member customer = memberRepository.findById(customerId)
                .orElseThrow(() -> new NoSuchElementException("회원 정보를 불러오는데 실패했습니다."));
        verifyingPermissions(customer);
        authorizationMember(customer);

        List<Pet> pets = customer.getPets();
        for (UpdatePetRequest request : requests) {
            Pet pet = pets.stream()
                    .filter(p -> p.getId() == request.getId())
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("반려견 조회에 실패했습니다."));

            UploadFile uploadFile = null;
            if (request.getProfileImage() != null && !request.getProfileImage().isEmpty()) { // 해당 반려견에 수정되는 프로필 이미지 사진이 있다면
                try {
                    uploadFile = fileUploadService.updateFile(request.getProfileImage(), "pets", pet.getProfileImage());
                } catch (IOException e) {
                    throw new FileUploadException("반려견 프로필 이미지 수정에 실패했습니다.", e);
                }
            }
            pet.update(
                    request.getName(), request.getAge(), request.getBreed(),
                    request.getMedicalConditions(), uploadFile != null ? uploadFile.getServerFileName() : null
            );
        }
        return pets.stream()
                .map(PetResponse.GetList::new)
                .toList();
    }

    private static void authorizationMember(Member member) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName(); // 로그인에 사용된 아이디 값 반환

        if(!member.getLoginId().equals(userName)) {
            throw new IllegalArgumentException("회원 본인만 가능합니다.");
        }
    }

    private static void verifyingPermissions(Member member) {
        if (!member.getRole().equals(Role.CUSTOMER)) {
            throw new IllegalArgumentException("고객만 반려견 등록,수정 및 삭제가 가능합니다.");
        }
    }
}
