package com.claim.claim_processing.integration.contribution.service;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.integration.contribution.dto.FullContributionHistoryResponse;

public interface AccountingYearSummaryService {
    
    /**
     * Get accounting year-wise summary with opening, transaction, and closing balances
     * No monthly breakdown - only yearly summary
     */
    ApiResponseDTO<FullContributionHistoryResponse> getAccountingYearSummary(String nppfNumber);
}
