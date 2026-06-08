package com.claim.claim_processing.application.service.detail;

import com.claim.claim_processing.application.DTO.request.detail.BeneficiarySettlementDetailRequestDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.detail.BeneficiarySettlementDetail;

public interface BeneficiarySettlementDetailService {

    BeneficiarySettlementDetail create(ClaimApplication claimApplication,
            BeneficiarySettlementDetailRequestDto request);

    BeneficiarySettlementDetail patch(BeneficiarySettlementDetailRequestDto request);

    void delete(Long id);
}