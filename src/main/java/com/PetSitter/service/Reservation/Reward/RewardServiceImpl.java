package com.PetSitter.service.Reservation.Reward;

import com.PetSitter.domain.Member.Member;
import org.springframework.stereotype.Service;

@Service
public class RewardServiceImpl implements RewardService {

    @Override
    public void addReward(Member customer, int amount) {
        int reservationAmount = calculatorReward(amount);
        customer.addRewardPoints(reservationAmount);
    }

    @Override
    public int calculatorReward(int reservationAmount) { // 적립금 계산 로직 (예약금의 2% 적립)
        return (int) (reservationAmount * 0.02);
    }
}
