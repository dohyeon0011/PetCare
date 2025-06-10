package com.PetSitter.domain.Reservation.SitterSchedule;

import com.PetSitter.domain.CareLog.CareLog;
import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Pet.PetReservation;
import com.PetSitter.domain.Reservation.CustomerReservation.CustomerReservation;
import com.PetSitter.domain.Reservation.CustomerReservation.ReservationStatus;
import com.PetSitter.dto.Reservation.SitterSchedule.response.SitterScheduleResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class SitterSchedule { // 돌봄 예약(돌봄사 시점)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sitter_schedule_id")
    private long id;

    @Comment("돌봄 예약(고객 시점)")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_reservation_id")
    @JsonIgnore
    private CustomerReservation customerReservation;

    @Comment("예약한 회원(고객) 번호")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Member customer;

    @Comment("예약된 회원(돌봄사) 번호")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sitter_id")
    private Member sitter;

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

//    @Comment("돌봄에 맡겨지는 반려견 목록")
//    @OneToMany(mappedBy = "sitterSchedule", cascade = CascadeType.ALL, orphanRemoval = true)
//    List<SitterPetReservation> sitterPetReservations = new ArrayList<>();

    @Comment("돌봄에 맡겨지는 반려견 목록")
    @OneToMany(mappedBy = "sitterSchedule", cascade = CascadeType.REMOVE, orphanRemoval = true)
    List<PetReservation> petReservations = new ArrayList<>();

    @Comment("예약이 발생한 시간")
    @CreatedDate
    @Column(name = "create_at")
    private LocalDateTime createdAt;

    @Comment("예약 상태")
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Comment("돌봄사가 작성한 케어 로그")
    @OneToMany(mappedBy = "sitterSchedule")
    List<CareLog> careLogList = new ArrayList<>();

//    @OneToMany
//    List<Review> reviews = new ArrayList<>();

    public static SitterSchedule createSitterReservation(CustomerReservation customerReservation) {
        SitterSchedule sitterSchedule = new SitterSchedule();
        sitterSchedule.addCustomer(customerReservation.getCustomer());
        sitterSchedule.addSitter(customerReservation.getSitter());

        for (PetReservation petReservation : customerReservation.getPetReservations()) {
            sitterSchedule.addPetReservation(petReservation);
        }

        sitterSchedule.addCustomerReservation(customerReservation);
        sitterSchedule.price = customerReservation.getPrice();
        sitterSchedule.changeAddress(customerReservation.getZipcode(), customerReservation.getAddress());
        sitterSchedule.requests = customerReservation.getRequests();
        sitterSchedule.reservationAt = customerReservation.getReservationAt();
        sitterSchedule.status = ReservationStatus.RESERVATION;

        return sitterSchedule;
    }

    public void addCustomer(Member customer) {
        this.customer = customer;
    }

    public void addSitter(Member sitter) {
        this.sitter = sitter;
        sitter.getSitterSchedules().add(this);
    }

    public void addCustomerReservation(CustomerReservation customerReservation) {
        this.customerReservation = customerReservation;
    }

//    public void addSitterPetReservation(SitterPetReservation sitterPetReservation) {
//        this.sitterPetReservations.add(sitterPetReservation);
//        sitterPetReservation.addSitterSchedule(this);
//    }

    public void addPetReservation(PetReservation petReservation) {
        this.petReservations.add(petReservation);
        petReservation.addSitterSchedule(this);
    }

    public void changeAddress(String zipcode, String address) {
        this.zipcode = zipcode;
        this.address = address;
    }

    public void changeReservationAt(LocalDate reservationAt) {
        this.reservationAt = reservationAt;
    }

    public void addCareLog(CareLog careLog) {
        this.careLogList.add(careLog);
    }

    public void cancel() {
        if (!this.status.equals(ReservationStatus.RESERVATION)) {
            throw new IllegalArgumentException("이미 취소된 예약입니다.");
        }
        this.status = ReservationStatus.CANCEL;
    }

    public SitterScheduleResponse.GetDetail toResponse(Integer usingPoint, int originalPrice) {
        return new SitterScheduleResponse.GetDetail(this.customer, this.sitter, this, this.petReservations, this.careLogList, usingPoint, originalPrice);
    }
}
