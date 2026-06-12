package com.claim.claim_processing.application.service.detail;

import com.claim.claim_processing.application.DTO.request.detail.LegalRecoveryDetailRequest;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.detail.LegalRecoveryDetail;

import java.util.List;

public interface LegalRecoveryService {

    LegalRecoveryDetail create(LegalRecoveryDetailRequest request, ClaimApplication claimApplication);

    LegalRecoveryDetail update(LegalRecoveryDetailRequest request, ClaimApplication claimApplication);

    LegalRecoveryDetail getById(Long id);

    LegalRecoveryDetail getByClaimApplicationId(Long claimApplicationId);
}