package com.claim.claim_processing.application.service.claimDetail;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.claim.claim_processing.application.DTO.response.application.GeneralClaimResponse;
import com.claim.claim_processing.application.DTO.response.claimDetail.GeneralClaimDetailResponse;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;

public interface ClaimDetailService {
    GeneralClaimDetailResponse create(GeneralClaimResponse rerequestResponse);
    ApiResponseDTO<Page<GeneralClaimDetailResponse>> getAllApprovedDetails(Pageable pageable);
}
