package com.claim.claim_processing.integration.contribution.service;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.integration.contribution.dto.FullContributionHistoryResponse;

public interface FullContributionHistoryService {
    ApiResponseDTO<FullContributionHistoryResponse> getFullContributionHistory(
            String nppfNumber);
}
