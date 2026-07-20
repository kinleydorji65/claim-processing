package com.claim.claim_processing.application.service.application;

import java.util.List;

import com.claim.claim_processing.application.DTO.request.application.ClaimApplicationForfeitedComponentRequestDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.application.ClaimApplicationForfeitedComponent;

public interface ClaimApplicationForfeitedComponentService {

    List<ClaimApplicationForfeitedComponent> saveForfeitedComponents(
            ClaimApplication claimApplication,
            List<ClaimApplicationForfeitedComponentRequestDto> forfeitedComponents
    );

    List<ClaimApplicationForfeitedComponent> patchForfeitedComponent(List<ClaimApplicationForfeitedComponentRequestDto> requests);
}
