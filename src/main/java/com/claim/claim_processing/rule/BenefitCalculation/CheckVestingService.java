package com.claim.claim_processing.rule.BenefitCalculation;

import com.claim.claim_processing.rule.dto.CheckVestingDto;
import com.claim.claim_processing.rule.dto.ClaimInitialPreviewRequest;

public interface CheckVestingService {
    CheckVestingDto checkVestingRules(ClaimInitialPreviewRequest request);
}
