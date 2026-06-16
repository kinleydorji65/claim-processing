package com.claim.claim_processing.application.service.workFlow;

import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationApprovalRequestDto;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationApprovalResponseDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;

public interface ClaimApplicationApprovalService {

    ApiResponseDTO<ClaimApplicationApprovalResponseDto> patch(
            String applicationNumber,
            ClaimApplicationApprovalRequestDto request
    );

    ApiResponseDTO<ClaimApplicationApprovalResponseDto> approve(
            String applicationNumber,
            ClaimApplicationApprovalRequestDto request
    );
     ApiResponseDTO<ClaimApplicationApprovalResponseDto> getByApplicationNumber(
            String applicationNumber
    );
}