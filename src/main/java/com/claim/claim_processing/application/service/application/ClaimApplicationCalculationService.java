package com.claim.claim_processing.application.service.application;

import java.math.BigDecimal;

import com.claim.claim_processing.application.DTO.request.application.ClaimApplicationCalculationPatchRequestDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.calculation.ClaimApplicationCalculationSummary;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO;

public interface ClaimApplicationCalculationService {
    ClaimApplicationCalculationSummary create(ClaimApplication claimApplication, ClaimCalculationResponseDTO calculationResponse, BigDecimal finalPayableAmount);
    ClaimApplicationCalculationSummary patch(ClaimApplicationCalculationPatchRequestDto request);
}
