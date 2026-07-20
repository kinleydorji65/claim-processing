package com.claim.claim_processing.application.service.application;

import com.claim.claim_processing.application.DTO.request.application.ClaimApplicationDeductionRequestDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.application.ClaimApplicationDeductionDetail;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO;

public interface ClaimApplicationDeductionDetailService {
    ClaimApplicationDeductionDetail saveCalculationDeductions(
            ClaimApplication claimApplication,
            ClaimApplicationDeductionRequestDto request);

    ClaimApplicationDeductionDetail patchDeductionDetail(ClaimApplicationDeductionRequestDto request);
}
