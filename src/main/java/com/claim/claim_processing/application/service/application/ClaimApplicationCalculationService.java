package com.claim.claim_processing.application.service.application;

import com.claim.claim_processing.application.DTO.request.application.ClaimApplicationCalculationPatchRequestDto;
import com.claim.claim_processing.application.DTO.request.application.ClaimApplicationOtherRequestDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.calculation.ClaimApplicationCalculationSummary;

public interface ClaimApplicationCalculationService {
    ClaimApplicationCalculationSummary create(ClaimApplication claimApplication, ClaimApplicationOtherRequestDto request);
    ClaimApplicationCalculationSummary patch(ClaimApplicationCalculationPatchRequestDto request);
}
