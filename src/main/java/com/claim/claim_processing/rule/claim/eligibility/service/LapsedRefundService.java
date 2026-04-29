package com.claim.claim_processing.rule.claim.eligibility.service;

import com.claim.claim_processing.rule.claim.DTO.request.EligibilityPreviewRequest;
import com.claim.claim_processing.rule.claim.DTO.response.LapsedRefundPreviewResponseDTO;

public interface LapsedRefundService {
    
    LapsedRefundPreviewResponseDTO previewLapsedRefund(EligibilityPreviewRequest request);
}
