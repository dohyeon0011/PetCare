package com.PetSitter.domain.Member;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.hibernate.annotations.Comment;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class CertificateListConverter implements AttributeConverter<List<String>, String> {

    private static final String SEPARATOR = ","; // 구분자

    @Comment("엔티티 컬럼 타입을 DB 컬럼 타입으로 변환")
    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        // List<String>을 String으로 변환
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        return String.join(SEPARATOR, attribute);
    }

    @Comment("DB 컬럼 타입을 엔티티 컬럼 타입으로 변환")
    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        // String을 List<String>으로 변환
        if (dbData == null || dbData.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(dbData.split(SEPARATOR))
                .map(String::trim)
                .collect(Collectors.toList());
    }

}
