package com.claim.claim_processing.rule.BenefitCalculation;

import com.claim.claim_processing.rule.dto.ClaimInitialPreviewRequest;
import com.claim.claim_processing.rule.dto.ForfeitedComponentResult;

public interface ForfeitedComponentService {
    
    /**
     * Process forfeited components, vesting check, and loan/rental info
     * Returns ONLY forfeited component codes, vesting info, loan info, rental info
     */
    ForfeitedComponentResult processForfeitedAndVestingComponents(
            ClaimInitialPreviewRequest request);
}