package com.PetSitter.service.Reservation.Reward;

import com.PetSitter.domain.Member.Member;

public interface RewardService {
    void addReward(Member customer, int amount); // 적립 메서드
    int calculatorReward(int reservationAmount); // 적립금 계산 메서드
}
