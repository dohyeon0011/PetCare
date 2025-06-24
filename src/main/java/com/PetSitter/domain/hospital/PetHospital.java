package com.PetSitter.domain.hospital;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Table
@Getter
public class PetHospital {  // 전국 동물 병원 정보 엔티티

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Comment("병원 이름")
    private String name;

    @Comment("소재지 우편번호")
    private String zipcode;

    @Comment("소재지 전체 주소")
    private String address;

    @Comment("도로명 우편번호")
    @Column(name = "street_zipcode")
    private String streetZipcode;

    @Comment("도로명 전체 주소")
    @Column(name = "street_address")
    private String streetAddress;

    @Comment("소재지 전화")
    private String tel;

    @Comment("위도")
    private Double lat;

    @Comment("경도")
    private Double lng;

    @Comment("데이터 갱신 일자")
    private LocalDateTime latestAt;

    @Builder
    public PetHospital(String name, String zipcode, String address, String streetZipcode, String streetAddress, String tel, Double lat, Double lng, LocalDateTime latestAt) {
        this.name = name;
        this.zipcode = zipcode;
        this.address = address;
        this.streetZipcode = streetZipcode;
        this.streetAddress = streetAddress;
        this.tel = tel;
        this.lat = lat;
        this.lng = lng;
        this.latestAt = latestAt;
    }

    /**
     * 병원 정보 업데이트
     */
    public void update(String tel, Double lat, Double lng, LocalDateTime latestAt) {
        this.tel = tel;
        this.lat = lat;
        this.lng = lng;
        this.latestAt = latestAt;
    }
}
