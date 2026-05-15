package com.claim.claim_processing.rule.claim.BenefitCalculation;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.rule.claim.DTO.contribution.PartialMemberContributionSummary;
import com.claim.claim_processing.rule.claim.DTO.request.ClaimPreviewRequest;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO;

public interface BenefitCalculationService {
    ApiResponseDTO<ClaimCalculationResponseDTO> calculateBenefit(ClaimPreviewRequest request);
    ApiResponseDTO<PartialMemberContributionSummary> getPartialContributionSummary(String nppfNumber);
}
