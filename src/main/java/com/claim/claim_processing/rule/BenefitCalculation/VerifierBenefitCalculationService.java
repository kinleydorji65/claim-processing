package com.claim.claim_processing.rule.BenefitCalculation;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.rule.claim.DTO.response.VerifierClaimCalculationResponseDTO;
import com.claim.claim_processing.rule.dto.ClaimInitialPreviewRequest;

public interface VerifierBenefitCalculationService {
    ApiResponseDTO<VerifierClaimCalculationResponseDTO> calculateBenefit(
            ClaimInitialPreviewRequest request);
}
