package com.PetSitter.service.Reservation.Reward;

public interface RewardService {
    void addReward(long customerId, int amount); // 적립 메서드
    int calculatorReward(int reservationAmount); // 적립금 계산 메서드
}
