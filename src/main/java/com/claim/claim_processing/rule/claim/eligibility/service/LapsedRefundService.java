package com.claim.claim_processing.rule.claim.eligibility.service;

import com.claim.claim_processing.rule.claim.DTO.request.ClaimPreviewRequest;
import com.claim.claim_processing.rule.claim.DTO.response.LapsedRefundPreviewResponseDTO;

public interface LapsedRefundService {
    
    LapsedRefundPreviewResponseDTO previewLapsedRefund(ClaimPreviewRequest request);
}
