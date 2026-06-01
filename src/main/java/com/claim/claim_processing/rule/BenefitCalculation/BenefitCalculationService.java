package com.claim.claim_processing.rule.BenefitCalculation;
// package com.claim.claim_processing.rule.claim.BenefitCalculation;

import java.math.BigDecimal;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO;
import com.claim.claim_processing.rule.dto.ClaimInitialPreviewRequest;

public interface BenefitCalculationService {
    ApiResponseDTO<ClaimCalculationResponseDTO> calculateBenefit(ClaimInitialPreviewRequest request);
    // ApiResponseDTO<BigDecimal> getFinalAmountAfterDeduction(BigDecimal calculatedAmount, Boolean isLoanThere);
}
