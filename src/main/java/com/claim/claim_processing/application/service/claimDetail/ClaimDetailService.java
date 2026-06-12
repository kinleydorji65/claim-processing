package com.claim.claim_processing.application.service.claimDetail;

import com.claim.claim_processing.application.DTO.response.application.GeneralClaimResponse;
import com.claim.claim_processing.application.DTO.response.claimDetail.GeneralClaimDetailResponse;

public interface ClaimDetailService {
    GeneralClaimDetailResponse create(GeneralClaimResponse rerequestResponse);
}
