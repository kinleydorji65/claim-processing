package com.claim.claim_processing.rule.ruleProcessing.service;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO;
import com.claim.claim_processing.rule.dto.ClaimInitialPreviewRequest;

public interface PartialWithdrawalRuleService {
    ApiResponseDTO<ClaimCalculationResponseDTO> calculatePartialWithdrawal(
            ClaimInitialPreviewRequest request
    );
}
