package com.claim.claim_processing.application.service.workFlow;

import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationVerificationRequestDto;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationVerificationResponseDto;

public interface ClaimApplicationVerificationService {

    ClaimApplicationVerificationResponseDto patch(
            Long claimApplicationId,
            ClaimApplicationVerificationRequestDto request
    );

    ClaimApplicationVerificationResponseDto verify(
            Long claimApplicationId,
            ClaimApplicationVerificationRequestDto request
    );

    ClaimApplicationVerificationResponseDto getByClaimApplicationId(
            Long claimApplicationId
    );
}