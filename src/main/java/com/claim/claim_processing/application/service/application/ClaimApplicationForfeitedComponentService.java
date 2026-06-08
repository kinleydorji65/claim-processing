package com.claim.claim_processing.application.service.application;

import java.util.List;

import com.claim.claim_processing.application.DTO.request.application.ClaimApplicationForfeitedComponentPatchRequestDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.application.ClaimApplicationForfeitedComponent;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO;

public interface ClaimApplicationForfeitedComponentService {

    List<ClaimApplicationForfeitedComponent> saveForfeitedComponents(
            ClaimApplication claimApplication,
            ClaimCalculationResponseDTO calculationResponse,
            String createdBy
    );

    List<ClaimApplicationForfeitedComponent> patchForfeitedComponent(List<ClaimApplicationForfeitedComponentPatchRequestDto> requests);
}
