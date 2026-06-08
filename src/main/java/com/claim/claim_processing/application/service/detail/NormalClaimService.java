package com.claim.claim_processing.application.service.detail;

import com.claim.claim_processing.application.DTO.request.detail.NormalClaimRequestDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.detail.NormalClaimDetail;

public interface NormalClaimService {

    NormalClaimDetail create(ClaimApplication claimApplication, NormalClaimRequestDto request);

    NormalClaimDetail update(ClaimApplication claimApplication, NormalClaimRequestDto request);
}