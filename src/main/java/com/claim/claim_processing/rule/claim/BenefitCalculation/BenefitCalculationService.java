package com.claim.claim_processing.rule.claim.BenefitCalculation;

import com.claim.claim_processing.rule.claim.DTO.request.ClaimPreviewRequest;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO;

public interface BenefitCalculationService {
    ClaimCalculationResponseDTO calculateBenefit(ClaimPreviewRequest request);
}
