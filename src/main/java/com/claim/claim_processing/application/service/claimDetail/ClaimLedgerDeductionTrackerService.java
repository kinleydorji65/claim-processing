package com.claim.claim_processing.application.service.claimDetail;

import com.claim.claim_processing.application.DTO.response.application.GeneralClaimResponse;
import com.claim.claim_processing.application.entity.claimDetail.ClaimDetail;
import com.claim.claim_processing.application.entity.claimDetail.ClaimLedgerDeductionTracker;

public interface ClaimLedgerDeductionTrackerService {
    ClaimLedgerDeductionTracker create(ClaimDetail claimDetail, GeneralClaimResponse generalClaimResponse, String createdBy);
}
