package com.PetSitter.service.Reservation.CustomerReservation;

import com.PetSitter.config.annotation.ReadOnlyTransactional;
import com.PetSitter.domain.CareAvailableDate.CareAvailableDate;
import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Member.Role;
import com.PetSitter.domain.Pet.Pet;
import com.PetSitter.domain.Pet.PetReservation;
import com.PetSitter.domain.PointHistory.PointsHistory;
import com.PetSitter.domain.PointHistory.PointsStatus;
import com.PetSitter.domain.Reservation.CustomerReservation.CustomerReservation;
import com.PetSitter.domain.Reservation.SitterSchedule.SitterSchedule;
import com.PetSitter.dto.Reservation.CustomerReservation.request.AddCustomerReservationRequest;
import com.PetSitter.dto.Reservation.CustomerReservation.response.CustomerReservationResponse;
import com.PetSitter.repository.CareAvailableDate.CareAvailableDateRepository;
import com.PetSitter.repository.Member.MemberRepository;
import com.PetSitter.repository.Pet.PetRepository;
import com.PetSitter.repository.PointHistory.PointHistoryRepository;
import com.PetSitter.repository.Reservation.CustomerReservation.CustomerReservationRepository;
import com.PetSitter.repository.Reservation.SitterSchedule.SitterScheduleRepository;
import com.PetSitter.service.Reservation.Reward.RewardService;
import com.PetSitter.service.Reservation.Reward.RewardServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerReservationService {

    private final MemberRepository memberRepository;
    private final PetRepository petRepository;
    private final CustomerReservationRepository customerReservationRepository;
    private final SitterScheduleRepository sitterScheduleRepository;
    private final CareAvailableDateRepository careAvailableDateRepository;
    private final RewardService rewardService = new RewardServiceImpl();
    private final PointHistoryRepository pointHistoryRepository;

    /**
     * 돌봄 예약 생성
     */
    @Transactional
    public CustomerReservationResponse.GetDetail save(AddCustomerReservationRequest request) {
        // 데드락을 방지하기 위해 락 획득 순서를 일관되게 돌봄사 먼저 조회(돌봄사의 회원 정보 수정과 고객의 예약이 동시에 발생할 경우를 대비) -> 락 획득 순서가 일관되지 않으면 서로 순환 대기가 발생해서 교착 상태에 빠져 하나는 롤백됨.
        Member sitter = memberRepository.findByIdAndFalseWithLock(request.getSitterId())
                .orElseThrow(() -> new NoSuchElementException("예약 오류: 돌봄사 정보 조회에 실패했습니다."));
        Member customer = memberRepository.findByIdAndFalseWithLock(request.getCustomerId())
                .orElseThrow(() -> new NoSuchElementException("예약 오류: 고객 정보 조회에 실패했습니다."));
        verifyingPermissionsSitter(sitter);
        verifyingPermissionsCustomer(customer);

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
        int resultPrice; // 결제될 최종 금액
        if (request.getAmount() > 0) { // 적립금을 사용하는 경우
            if (request.getAmount() > customer.getAmount()) {
                throw new IllegalArgumentException("현재 보유 중인 적립금보다 큰 수를 입력하셨습니다. 다시 입력하세요.");
            }
            resultPrice = careAvailableDate.getPrice() - request.getAmount(); // 원래 돌봄 비용 - 적립금 사용 금액

            customerReservation = CustomerReservation.createCustomerReservation(customer, sitter, resultPrice, request.getRequests(), petReservations.toArray(new PetReservation[0])); // 차감한 적립금 만큼 결제 금액 산정

            PointsHistory.builder() // CustomerReservaiton에 CascadeType.PERSIST 옵션 줘서 repository save()로 영속화 안해줘도 됨.(적립금 사용 내역)
                    .customerReservation(customerReservation)
                    .customer(customer)
                    .point(request.getAmount())
                    .pointsStatus(PointsStatus.USING)
                    .build();
            customer.subRewardPoints(request.getAmount()); // 사용한 적립금 차감
        } else { // 적립금을 사용하지 않는 경우
            resultPrice = careAvailableDate.getPrice();
            customerReservation = CustomerReservation.createCustomerReservation(customer, sitter, resultPrice, request.getRequests(), petReservations.toArray(new PetReservation[0]));
        }
        // 고객 시점 돌봄 예약 생성
        customerReservation.changeReservationAt(careAvailableDate.getAvailableAt());

        // 돌봄사 시점 돌봄 예약 생성
        sitterReservation(customerReservation, careAvailableDate);
        rewardService.addReward(customer, resultPrice); // 적립금을 사용 유무에 상관 없이 무조건 결제 금액 만큼 적립

        PointsHistory.builder() // 적립금 적립 내역
                .customerReservation(customerReservation)
                .customer(customer)
                .point(rewardService.calculatorReward(resultPrice))
                .pointsStatus(PointsStatus.SAVING)
                .build();

        customerReservationRepository.save(customerReservation);
        return customerReservation.toResponse(new ArrayList<>(), null, careAvailableDate.getPrice());
    }

    @Transactional  // 전파 범위를 상위 트랜잭션에 포함되게 설정
    private static void sitterReservation(CustomerReservation customerReservation, CareAvailableDate careAvailableDate) {
        SitterSchedule sitterReservation = SitterSchedule.createSitterReservation(customerReservation);
        sitterReservation.changeReservationAt(careAvailableDate.getAvailableAt());
        careAvailableDate.reservation();
    }

    /**
     * 특정 회원의 돌봄 예약 내역 전체 조회
     * @ReadOnlyTransactional: 커스텀 읽기 전용 어노테이션
     */
    @ReadOnlyTransactional
    public Page<CustomerReservationResponse.GetList> findAllById(long customerId, Pageable pageable) {
        Member customer = memberRepository.findById(customerId)
                .orElseThrow(() -> new NoSuchElementException("예약 조회 오류 : 현재 회원은 존재하지 않는 회원입니다."));
        authorizationMember(customer);
        return customerReservationRepository.findByCustomerId(customerId, pageable);
    }

    /**
     * 특정 회원의 총 예약 금액 조회
     */
    public Long getTotalReservationAmount(Long customerId) {
        return customerReservationRepository.findTotalReservationAmountByCustomerId(customerId);
    }

    /**
     * 특정 회원의 특정 돌봄 예약 조회
     */
    @Transactional(readOnly = true)
    public CustomerReservationResponse.GetDetail findById(long customerId, long customerReservationId) {
        CustomerReservation customerReservation = customerReservationRepository.findByCustomerIdAndId(customerId, customerReservationId)
                .orElseThrow(() -> new NoSuchElementException("해당 돌봄 예약 내역이 존재하지 않습니다."));
        authorizationMember(customerReservation.getCustomer());

        SitterSchedule sitterSchedule = sitterScheduleRepository.findByCustomerReservation(customerReservation)
                .orElseThrow(() -> new NoSuchElementException("돌봄사에게 해당 예약이 존재하지 않습니다."));

        CareAvailableDate careAvailableDate = careAvailableDateRepository.findBySitterIdAndAvailableAt(sitterSchedule.getSitter().getId(), sitterSchedule.getReservationAt())
                .orElseThrow(() -> new EntityNotFoundException("해당 돌봄 예약 날짜가 존재하지 않습니다. (" + sitterSchedule.getReservationAt() + ")"));

        Optional<PointsHistory> usingPointHistory = pointHistoryRepository.findByCustomerReservationAndPointsStatus(customerReservation, PointsStatus.USING);
        Integer usingPoint = null;
        if (usingPointHistory.isPresent()) {    // 고객이 해당 돌봄 예약 건에 사용한 포인트가 있을 경우
            usingPoint = usingPointHistory.get().getPoint();
        }

        return customerReservation.toResponse(sitterSchedule.getCareLogList().stream()
                .filter(c -> !c.isDeleted()).toList(), usingPoint, careAvailableDate.getPrice());
    }

    /**
     * 특정 회원의 특정 돌봄 예약 취소
     */
    @Transactional
    public void delete(long customerId, long customerReservationId) {
        CustomerReservation customerReservation = customerReservationRepository.findByCustomerIdAndId(customerId, customerReservationId)
                .orElseThrow(() -> new NoSuchElementException("회원에게 해당 돌봄 예약 내역이 존재하지 않습니다."));

        // 순환 대기로 데드락을 방지하기 위해 (돌봄사 시점)예약 취소와 동일하게 고객 -> 돌봄사 순으로 락 획득 조회.
        Member customer = memberRepository.findByIdAndFalseWithLock(customerId)
                .orElseThrow(() -> new NoSuchElementException("로그인한 회원 정보를 불러오는데 실패했습니다."));
        Member sitter = memberRepository.findByIdAndFalseWithLock(customerReservation.getSitter().getId())
                .orElseThrow(() -> new NoSuchElementException("돌봄사의 정보를 조회하는데 실패했습니다."));
        authorizationMember(customer);
        verifyingPermissionsCustomer(customer);
        verifyingPermissionsSitter(sitter);

        CareAvailableDate careAvailableDate = memberRepository.findCareAvailableDateBySitterIdAndAvailableAt(sitter.getId(), customerReservation.getReservationAt())
                .orElseThrow(() -> new NoSuchElementException("예약 취소 오류: 돌봄사에게 해당 예약 날짜를 찾을 수 없습니다."));

        SitterSchedule sitterSchedule = sitterScheduleRepository.findByCustomerReservation(customerReservation)
                .orElseThrow(() -> new NoSuchElementException("예약 취소 오류: 돌봄사에게 해당 예약이 존재하지 않습니다."));

        Optional<PointsHistory> usingPoints = pointHistoryRepository.findByCustomerReservationAndPointsStatus(customerReservation, PointsStatus.USING);
        Optional<PointsHistory> savingPoints = pointHistoryRepository.findByCustomerReservationAndPointsStatus(customerReservation, PointsStatus.SAVING);
        usingPoints.ifPresentOrElse( // 해당 예약 건에 적립금을 사용한 경우
                pointsHistory -> {
                    customer.addRewardPoints(pointsHistory.getPoint()); // 사용한 적립금 반환
                    customer.subRewardPoints(savingPoints.get().getPoint()); // 적립된 적립금 회수
                },
                () -> customer.subRewardPoints(savingPoints.get().getPoint()) // 적립됨 적립금 회수(해당 예약 건에 사용하지 않은 경우)
        );
        reservationCancel(careAvailableDate, customerReservation, sitterSchedule);
    }

    private static void authorizationMember(Member member) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName(); // 로그인에 사용된 아이디 값 반환

        if(!member.getLoginId().equals(userName)) {
            throw new IllegalArgumentException("회원 본인만 가능합니다.");
        }
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

    @Transactional // 전파 범위를 상위 트랜잭션에 속하게 설정
    private static void reservationCancel(CareAvailableDate careAvailableDate, CustomerReservation customerReservation, SitterSchedule sitterSchedule) {
        careAvailableDate.cancel();
        customerReservation.cancel();
        sitterSchedule.cancel();
    }
}
