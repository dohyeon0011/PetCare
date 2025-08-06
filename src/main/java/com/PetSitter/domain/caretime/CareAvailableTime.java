package com.PetSitter.domain.caretime;

import com.PetSitter.domain.CareAvailableDate.CareAvailableDate;
import com.PetSitter.domain.CareAvailableDate.CareAvailableDateStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CareAvailableTime { // 예약 가능 시간(CareAvailableDate의 자식)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "care_available_date_id")
    private CareAvailableDate careAvailableDate;

    @Comment("예약 가능 시간")
    @Column(name = "available_at")
    private LocalTime availableAt;

    @Comment("돌봄 비용")
    private int price;

    @Comment("예약 가능 상태")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CareAvailableDateStatus status;

    @Builder
    public CareAvailableTime(CareAvailableDate careAvailableDate, LocalTime availableAt, int price) {
        addCareAvailableDate(careAvailableDate);
        this.availableAt = availableAt;
        this.price = price;
        this.status = CareAvailableDateStatus.POSSIBILITY;
    }

    /**
     * 예약 가능 날짜(CareAvailableDate) 연관관계 설정 편의 메서드
     */
    public void addCareAvailableDate(CareAvailableDate careAvailableDate) {
        this.careAvailableDate = careAvailableDate;
    }

    /**
     * 예약 상태 변경
     */
    public void reservation() {
        if (!status.equals(CareAvailableDateStatus.POSSIBILITY)) {
            throw new IllegalArgumentException("요청하신 " + this.availableAt + " 이 시간은 이미 예약된 시간이거나 돌봄사가 삭제한 시간 입니다.");
        }
        this.status = CareAvailableDateStatus.IMPOSSIBILITY;
    }

    /**
     * 예약 취소
     */
    public void cancel() {
        if (!this.status.equals(CareAvailableDateStatus.IMPOSSIBILITY)) {
            throw new IllegalArgumentException("요청하신 " + this.availableAt + "이 시간은 이미 취소된 예약입니다.");
        }
        this.status = CareAvailableDateStatus.POSSIBILITY;
    }


}
