package com.claim.claim_processing.rule.claim.eligibility.service;

import com.claim.claim_processing.rule.claim.DTO.request.ClaimEligibilityCheckRequest;
import com.claim.claim_processing.rule.claim.DTO.request.ClaimEligibilityPreviewRequest;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimEligibilityPreviewResponse;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimEligibilityResponse;

public interface ClaimEligibilityRuleService {

    ClaimEligibilityPreviewResponse previewEligibility(ClaimEligibilityPreviewRequest request);

    // ClaimEligibilityResponse checkEligibility(ClaimEligibilityCheckRequest request);
}
