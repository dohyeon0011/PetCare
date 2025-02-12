package com.PetSitter.service.Reservation.CustomerReservation;

import com.PetSitter.domain.CareAvailableDate.CareAvailableDate;
import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.Role;
import com.PetSitter.domain.Pet.Pet;
import com.PetSitter.domain.Pet.PetReservation;
import com.PetSitter.domain.Reservation.CustomerReservation.CustomerReservation;
import com.PetSitter.domain.Reservation.SitterSchedule.SitterSchedule;
import com.PetSitter.dto.Reservation.CustomerReservation.request.AddCustomerReservationRequest;
import com.PetSitter.dto.Reservation.CustomerReservation.response.CustomerReservationResponse;
import com.PetSitter.repository.CareAvailableDate.CareAvailableDateRepository;
import com.PetSitter.repository.Member.MemberRepository;
import com.PetSitter.repository.Pet.PetRepository;
import com.PetSitter.repository.Reservation.CustomerReservation.CustomerReservationRepository;
import com.PetSitter.repository.Reservation.SitterSchedule.SitterScheduleRepository;
import com.PetSitter.service.Reservation.Reward.RewardServiceImpl;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CustomerReservationService {

    private final MemberRepository memberRepository;
    private final PetRepository petRepository;
    private final CustomerReservationRepository customerReservationRepository;
    private final SitterScheduleRepository sitterScheduleRepository;
    private final CareAvailableDateRepository careAvailableDateRepository;
    private final RewardServiceImpl rewardService;

    @Transactional
    public CustomerReservationResponse.GetDetail save(AddCustomerReservationRequest request) {
        Member customer = memberRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new NoSuchElementException("예약 오류: 고객 정보 조회에 실패했습니다."));

        verifyingPermissionsCustomer(customer);

        // 예약 배정되는 돌봄사 찾고 돌봄사가 등록했던 날짜중 선택된 날짜 찾아서 해당 날짜 예약 상태 바꾸기
        Member sitter = memberRepository.findById(request.getSitterId())
                .orElseThrow(() -> new NoSuchElementException("예약 오류: 돌봄사 정보 조회에 실패했습니다."));

        verifyingPermissionsSitter(sitter);

        // 예약으로 선택된 날짜 찾기
        CareAvailableDate careAvailableDate = careAvailableDateRepository.findBySitterIdAndId(sitter.getId(), request.getCareAvailableId())
                .orElseThrow(() -> new NoSuchElementException("예약 오류: 돌봄사가 해당 날짜를 돌봄 가능 날짜로 등록하지 않았습니다."));

        // 돌봄에 배정될 반려견 중간 매핑 엔티티
        List<PetReservation> petReservations = request.getPetIds().stream()
                .map(petId -> {
                    Pet pet = petRepository.findByCustomerIdAndId(customer.getId(), petId)
                            .orElseThrow(() -> new NoSuchElementException("반려견 정보 조회에 실패했습니다."));
                    return PetReservation.createPetReservation(pet);
                })
                .toList();

        CustomerReservation customerReservation;
        int resultPrice; // 결제될 금액

        if (request.getAmount() > 0) { // 적립금을 사용하는 경우
            if (request.getAmount() > customer.getAmount()) {
                throw new IllegalArgumentException("현재 보유 중인 적립금보다 큰 수를 입력하셨습니다. 다시 입력하세요.");
            }
            resultPrice = careAvailableDate.getPrice() - request.getAmount(); // 원래 돌봄 비용 - 적립금 사용 금액

            customerReservation = CustomerReservation.createCustomerReservation(customer, sitter, resultPrice, request.getRequests(), petReservations.toArray(new PetReservation[0])); // 차감한 적립금 만큼 결제 금액 산정
            customer.subRewardPoints(request.getAmount()); // 사용한 적립금 차감
        } else { // 적립금을 사용하지 않는 경우
            resultPrice = careAvailableDate.getPrice();
            customerReservation = CustomerReservation.createCustomerReservation(customer, sitter, careAvailableDate.getPrice(), request.getRequests(), petReservations.toArray(new PetReservation[0]));
        }

        // 고객 시점 돌봄 예약 생성
        customerReservation.changeReservationAt(careAvailableDate.getAvailableAt());

        // 돌봄사 시점 돌봄 예약 생성
        SitterSchedule sitterReservation = SitterSchedule.createSitterReservation(customerReservation);
        sitterReservation.changeReservationAt(careAvailableDate.getAvailableAt());
        careAvailableDate.reservation();
        rewardService.addReward(customer.getId(), resultPrice);

        customerReservationRepository.save(customerReservation);

        return customerReservation.toResponse(new ArrayList<>());
    }

    @Comment("특정 회원의 돌봄 예약 내역 전체 조회")
    @Transactional(readOnly = true)
    public Page<CustomerReservationResponse.GetList> findAllById(long customerId, Pageable pageable) {
        Member customer = memberRepository.findById(customerId)
                .orElseThrow(() -> new NoSuchElementException("예약 조회 오류 : 현재 회원은 존재하지 않는 회원입니다."));

        authorizationMember(customer);

//        List<CustomerReservationResponse.GetList> reservations = customerReservationRepository.findByCustomerId(customer.getId())
//                .stream()
//                .map(CustomerReservationResponse.GetList::new) // Constructor Reference 사용
//                .collect(Collectors.toList());

        Page<CustomerReservationResponse.GetList> reservations = customerReservationRepository.findByCustomerId(customerId, pageable);

        return reservations;
    }

    @Comment("특정 회원의 특정 돌봄 예약 조회")
    @Transactional(readOnly = true)
    public CustomerReservationResponse.GetDetail findById(long customerId, long customerReservationId) {
        CustomerReservation customerReservation = customerReservationRepository.findByCustomerIdAndId(customerId, customerReservationId)
                .orElseThrow(() -> new NoSuchElementException("해당 돌봄 예약 내역이 존재하지 않습니다."));

        authorizationMember(customerReservation.getCustomer());

        SitterSchedule sitterSchedule = sitterScheduleRepository.findByCustomerReservation(customerReservation)
                .orElseThrow(() -> new NoSuchElementException("돌봄사에게 해당 예약이 존재하지 않습니다."));

        return customerReservation.toResponse(sitterSchedule.getCareLogList());
    }

    @Comment("특정 회원의 특정 돌봄 예약 취소")
    @Transactional
    public void delete(long customerId, long customerReservationId) {
        Member customer = memberRepository.findById(customerId)
                .orElseThrow(() -> new NoSuchElementException("로그인한 회원 정보를 불러오는데 실패했습니다."));

        CustomerReservation customerReservation = customerReservationRepository.findByCustomerIdAndId(customer.getId(), customerReservationId)
                .orElseThrow(() -> new NoSuchElementException("회원에게 해당 돌봄 예약 내역이 존재하지 않습니다."));

        authorizationMember(customer);
        verifyingPermissionsCustomer(customer);

        Member sitter = memberRepository.findById(customerReservation.getSitter().getId())
                .orElseThrow(() -> new NoSuchElementException("돌봄사의 정보를 조회하는데 실패했습니다."));

        verifyingPermissionsSitter(sitter);

        CareAvailableDate careAvailableDate = memberRepository.findCareAvailableDateBySitterIdAndAvailableAt(sitter.getId(), customerReservation.getReservationAt())
                .orElseThrow(() -> new NoSuchElementException("예약 취소 오류: 돌봄사에게 해당 예약 날짜를 찾을 수 없습니다."));

        SitterSchedule sitterSchedule = sitterScheduleRepository.findByCustomerReservation(customerReservation)
                .orElseThrow(() -> new NoSuchElementException("예약 취소 오류: 돌봄사에게 해당 예약이 존재하지 않습니다."));

        careAvailableDate.cancel();
        customerReservation.cancel();
        sitterSchedule.cancel();

//        customerReservationRepository.delete(customerReservation);
    }

    private static void authorizationMember(Member member) {
//        String userName = SecurityContextHolder.getContext().getAuthentication().getName(); // 로그인에 사용된 아이디 값 반환
//
//        if(!member.getLoginId().equals(userName)) {
//            throw new IllegalArgumentException("회원 본인만 가능합니다.");
//        }
    }

    public static void verifyingPermissionsCustomer(Member customer) {
        if (!customer.getRole().equals(Role.CUSTOMER)) {
            throw new IllegalArgumentException("고객만 예약 등록,수정,조회 및 삭제가 가능합니다.");
        }
    }

    public static void verifyingPermissionsSitter(Member sitter) {
        if (!sitter.getRole().equals(Role.PET_SITTER)) {
            throw new IllegalArgumentException("돌봄 예약 배정,수정 및 삭제는 돌봄사만 가능합니다.");
        }
    }

}
