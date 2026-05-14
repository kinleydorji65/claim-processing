package com.claim.claim_processing.rule.claim.BenefitCalculation;

import java.math.BigDecimal;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.rule.claim.DTO.request.ClaimPreviewRequest;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO;

public interface BenefitCalculationService {
    ApiResponseDTO<ClaimCalculationResponseDTO> calculateBenefit(ClaimPreviewRequest request);
    // ApiResponseDTO<BigDecimal> getTotalAccumulationAmount(String memberCode);
}
