package com.PetSitter.domain.Certification;

import com.PetSitter.domain.Member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Comment("돌봄사 자격증 엔티티")
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certification_id", updatable = false)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sitter_id")
    private Member sitter;

    @Comment("자격증 이름")
    private String name;

    @Builder
    public Certification(String name) {
        this.name = name;
    }

    // 돌봄사-자격증 연관관계 편의 메서드
    public void addSitter(Member sitter) {
        this.sitter = sitter;
        sitter.getCertifications().add(this);
    }

    public void update(String name) {
        this.name = name;
    }

}
