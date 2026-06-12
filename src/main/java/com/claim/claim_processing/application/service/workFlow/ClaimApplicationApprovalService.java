package com.claim.claim_processing.application.service.workFlow;

import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationApprovalRequestDto;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationApprovalResponseDto;

public interface ClaimApplicationApprovalService {

    ClaimApplicationApprovalResponseDto patch(
            Long claimApplicationId,
            ClaimApplicationApprovalRequestDto request
    );

    ClaimApplicationApprovalResponseDto approve(
            Long claimApplicationId,
            ClaimApplicationApprovalRequestDto request
    );

    ClaimApplicationApprovalResponseDto getByClaimApplicationId(
            Long claimApplicationId
    );
}