package com.PetSitter.domain.Reservation.CustomerReservation;

import com.PetSitter.domain.CareLog.CareLog;
import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Pet.PetReservation;
import com.PetSitter.domain.PointHistory.PointsHistory;
import com.PetSitter.domain.Review.Review;
import com.PetSitter.dto.Reservation.CustomerReservation.response.CustomerReservationResponse;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class CustomerReservation { // 돌봄 예약(고객 시점)

    @Id
    @GeneratedValue
    @Column(name = "customer_reservation_id", updatable = false)
    private long id;

    @Comment("예약한 회원(고객) 번호")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Member customer;

    @Comment("예약된 회원(돌봄사) 번호")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sitter_id")
    private Member sitter;

//    @Comment("돌봄에 맡기는 반려견 목록")
//    @OneToMany(mappedBy = "customerReservation", cascade = CascadeType.ALL)
//    private List<CustomerPetReservation> customerPetReservations = new ArrayList<>();

    @OneToMany(mappedBy = "customerReservation", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<PetReservation> petReservations = new ArrayList<>();

    @Comment("예약된 날짜")
    @Column(name = "reservation_at")
    private LocalDate reservationAt;

    @Comment("돌봄 예약 비용")
    private int price;

    @Comment("돌봄 장소 주소(우편번호)")
    private String zipcode;

    @Comment("돌봄 장소 주소(상세주소)")
    private String address;

    @Comment("고객 요청 사항")
    private String requests;

    @Comment("예약이 발생한 시간")
    @CreatedDate
    @Column(name = "create_at")
    private LocalDateTime createdAt;

    @Comment("예약 상태")
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "customerReservation", orphanRemoval = true)
    private Review review;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customerReservation", cascade = CascadeType.PERSIST)
    private List<PointsHistory> pointsHistory = new ArrayList<>();

    public static CustomerReservation createCustomerReservation(Member customer, Member sitter, int price, String requests, PetReservation... petReservations) {
        CustomerReservation customerReservation = new CustomerReservation();
        customerReservation.addCustomer(customer);
        customerReservation.addSitter(sitter);

        for (PetReservation petReservation : petReservations) {
            customerReservation.addPetReservation(petReservation);
        }
        customerReservation.price = price;
        customerReservation.requests = requests;
        customerReservation.changeAddress(sitter);
        customerReservation.status = ReservationStatus.RESERVATION;

        return customerReservation;
    }

    // 예약(고객 시점) - 회원(고객) 연관관계 편의 메서드
    public void addCustomer(Member customer) {
        this.customer = customer;
        customer.getCustomerReservations().add(this);
    }

    // 예약(고객 시점) - 회원(돌봄사) 연관관계 편의 메서드
    public void addSitter(Member sitter) {
        this.sitter = sitter;
    }

    // 예약(고객 시점) - 중간 매핑 테이블 연관관계 편의 메서드
//    public void addPetReservation(CustomerPetReservation customerPetReservation) {
//        this.customerPetReservations.add(customerPetReservation);
//        customerPetReservation.addCustomerReservation(this);
//    }

    public void addPetReservation(PetReservation petReservation) {
        this.petReservations.add(petReservation);
        petReservation.addCustomerReservation(this);
    }

    public void addReview(Review review) {
        this.review = review;
    }

    public void addPointsHistory(PointsHistory pointsHistory) {
        this.pointsHistory.add(pointsHistory);
    }

    // 고객이 예약한 날짜 설정
    public void changeReservationAt(LocalDate reservationAt) {
        this.reservationAt = reservationAt;
    }

    // 돌봄 장소 설정
    public void changeAddress(Member sitter) {
        this.zipcode = sitter.getZipcode();
        this.address = sitter.getAddress();
    }

    // 예약 취소
    public void cancel() {
        if (!this.status.equals(ReservationStatus.RESERVATION)) {
            throw new IllegalArgumentException("이미 취소된 예약입니다.");
        }
        this.status = ReservationStatus.CANCEL;
    }

    // 해당 예약 상세 조회
    public CustomerReservationResponse.GetDetail toResponse(List<CareLog> careLogList, Integer usingPoint, int originalPrice) {
        return new CustomerReservationResponse.GetDetail(this.customer, this.sitter, this, this.petReservations, careLogList, this.review, usingPoint, originalPrice);
    }
}

