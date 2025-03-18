package com.PetSitter.dto.Pet.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
public class UpdatePetRequests {

    private List<UpdatePetRequest> updatePetRequests = new ArrayList<>();

}
