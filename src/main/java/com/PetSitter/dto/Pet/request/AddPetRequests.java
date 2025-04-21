package com.PetSitter.dto.Pet.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @ModelAttribute를 이용해서 AddPetRequest를 List<>로 받기 위해 별도의 DTO를 생성.
 * List<AddPetRequest>와 같은 복합 객체(리스트나 배열)를 @ModelAttribute로 직접 받는 것은 기본적으로 지원 X
 * 이유는 Spring이 List의 각 항목에 대한 매핑 방식을 처리할 때 명확한 규칙을 찾기 어려워하기 때문.
 * 즉, Spring이 각 항목을 어떻게 매핑해야 하는지 이해하는데 한계가 있을 수 있음.
 * 특정 필드가 여러 개일 때의 문제:
 * 예를 들어, @ModelAttribute로 처리할 수 있는 단일 객체는 폼 데이터가 name, age, breed와 같은 각각의 필드로 나뉘어 있어 하나하나 바인딩이 가능.
 * 그러나 List<AddPetRequest>로 받으려면 각각의 리스트 항목을 어떻게 처리할지에 대한 명확한 규칙을 제공해야 함.
 * Spring은 List<AddPetRequest>를 단일 객체로 묶어서 처리하기 어려운 경우가 발생할 수 있음.
 */
@NoArgsConstructor
@Setter
@Getter
public class AddPetRequests {

//    @JsonProperty("petReq") 지금처럼 @ModelAttribute로 폼데이터를 받을 때에는 적용이 안됨.(JSON 역직렬화가 아닌, 파라미터 바인딩을 하기 때문에 List<> 클래스 필드명에 맞춰서 매핑되기 때문)
    private List<AddPetRequest> requests = new ArrayList<>();

}
