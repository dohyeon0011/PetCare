package com.PetSitter.domain.report.user;

import com.PetSitter.domain.Member.Member;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Getter
public class UserReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("신고한 회원")
    @ManyToOne
    @JoinColumn(name = "reporter_id")
    private Member reporter;

    @Comment("신고 당한 회원")
    @ManyToOne
    @JoinColumn(name = "reported_id")
    private Member reportedUser;

    @Comment("신고 문의 제목")
    private String title;

    @Comment("신고 내용")
    @Lob
    @Column(nullable = false)
    private String content;

    @Comment("처리 상태")
    @Enumerated(value = EnumType.STRING)
    private ReportStatus status;

    @Comment("작성 시간")
    @CreatedDate
    private LocalDateTime createdAt;

    @Comment("삭제 여부: True = 삭제, False = 존재")
    private boolean isDeleted;

    @Builder
    public UserReport(Member reporter, Member reportedUser, String title, String content) {
        addReporter(reporter);
        addReportedUser(reportedUser);
        this.title = title;
        this.content = content;
        this.status = ReportStatus.PENDING;
    }

    /**
     * 신고한 회원 연관관계 설정 편의 메서드
     */
    private void addReporter(Member reporter) {
        this.reporter = reporter;
    }

    /**
     * 신고 당한 회원 연관관계 설정 편의 메서드
     */
    private void addReportedUser(Member reportedUser) {
        this.reportedUser = reportedUser;
    }

    /**
     * 삭제 여부 수정 커스텀 메서드
     */
    public void changeIsDelete() {
        if (this.isDeleted) {
            throw new IllegalArgumentException("해당 문의는 이미 삭제 처리가 된 상태입니다. {id=" + this.id + "}");
        }
        this.isDeleted = Boolean.TRUE;
    }

    /**
     * 문의 처리 진행 현황 상태 수정 커스텀 메서드
     */
    public void changeReportStatus(ReportStatus status) {
        this.status = status;
    }
}
