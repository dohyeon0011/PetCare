package com.PetSitter.domain.PointHistory;

import com.PetSitter.domain.Member.Member;
import com.PetSitter.domain.Reservation.CustomerReservation.CustomerReservation;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "points_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Getter
public class PointsHistory { // 고객의 포인트 내역(적립, 사용)

    @Id
    @GeneratedValue
    @Column(name = "points_history_id", updatable = false)
    private long id;

    @Comment("고객 시점 예약")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_reservation_id", nullable = false)
    private CustomerReservation customerReservation;

    @Comment("포인트 사용 고객")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Member customer;

    @Comment("적립/사용 포인트")
    private int point;

    @Comment("포인트 적립 or 사용 시간")
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Comment("포인트 적립 or 사용")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PointsStatus pointsStatus;

    @Builder
    public PointsHistory(CustomerReservation customerReservation, Member customer, int point, PointsStatus pointsStatus) {
        addCustomerReservation(customerReservation);
        addCustomer(customer);
        this.point = point;
        this.pointsStatus = pointsStatus;
    }

    // 포인트 사용 내역 - 예약(고객 시점) 연관관계 편의 메서드
    public void addCustomerReservation(CustomerReservation customerReservation) {
        this.customerReservation = customerReservation;
        customerReservation.getPointsHistory().add(this);
    }

    // 포인트 사용 내역 - 포인트 사용 고객 연관관계 편의 메서드
    public void addCustomer(Member customer) {
        this.customer = customer;
    }
}
