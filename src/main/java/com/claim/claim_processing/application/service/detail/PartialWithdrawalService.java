package com.claim.claim_processing.application.service.detail;

import com.claim.claim_processing.application.DTO.request.detail.PartialWithdrawalRequestDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.detail.PartialWithdrawalDetail;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;

public interface PartialWithdrawalService {

    PartialWithdrawalDetail create(ClaimApplication claimApplication, PartialWithdrawalRequestDto request);

    PartialWithdrawalDetail update(PartialWithdrawalRequestDto request);
    ApiResponseDTO<Void> delete(Long id);
}