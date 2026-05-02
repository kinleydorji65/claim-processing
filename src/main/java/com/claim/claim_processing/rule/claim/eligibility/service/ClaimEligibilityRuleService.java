package com.claim.claim_processing.rule.claim.eligibility.service;

import com.claim.claim_processing.rule.claim.DTO.request.ClaimEligibilityCheckRequest;
import com.claim.claim_processing.rule.claim.DTO.request.ClaimPreviewRequest;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimEligibilityPreviewResponse;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimEligibilityResponse;

public interface ClaimEligibilityRuleService {

    ClaimEligibilityPreviewResponse previewEligibility(ClaimPreviewRequest request);

    // ClaimEligibilityResponse checkEligibility(ClaimEligibilityCheckRequest request);
}
