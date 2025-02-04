package com.PetSitter.dto.Certification.response;

import com.PetSitter.domain.Certification.Certification;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class CertificationResponse {

    @NoArgsConstructor
    @Getter
    public static class GetList {
        private long id;
        private String name;

        public GetList(Certification certification) {
            this.id = certification.getId();
            this.name = certification.getName();
        }
    }

    @NoArgsConstructor
    @Getter
    public static class GetReservation {
        private String name;

        public GetReservation(Certification certification) {
            this.name = certification.getName();
        }
    }

}
