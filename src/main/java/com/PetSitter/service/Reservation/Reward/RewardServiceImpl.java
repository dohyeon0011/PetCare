package com.PetSitter.service.Reservation.Reward;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.repository.Member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class RewardServiceImpl implements RewardService {

    private final MemberRepository memberRepository;

    @Override
    public void addReward(long customerId, int amount) {
        Member customer = memberRepository.findById(customerId)
                .orElseThrow(() -> new NoSuchElementException("고객 정보 조회에 실패했습니다."));

        int reservationAmount = calculatorReward(amount);

        customer.addRewardPoints(reservationAmount);
    }

    @Override
    public int calculatorReward(int reservationAmount) { // 적립금 계산 로직 (예약금의 2% 적립)
        return (int) (reservationAmount * 0.02);
    }

}
