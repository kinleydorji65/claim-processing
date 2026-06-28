package com.claim.claim_processing.application.service.workFlow;

import java.util.List;

import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationApprovalRequestDto;
import com.claim.claim_processing.application.DTO.response.claimDetail.GeneralClaimDetailResponse;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationApprovalResponseDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;

public interface ClaimApplicationApprovalService {

        ApiResponseDTO<ClaimApplicationApprovalResponseDto> patch(
                        String applicationNumber,
                        ClaimApplicationApprovalRequestDto request);

        ApiResponseDTO<GeneralClaimDetailResponse> approve(
                        String applicationNumber,
                        ClaimApplicationApprovalRequestDto request);

        ApiResponseDTO<ClaimApplicationApprovalResponseDto> getByApplicationNumber(
                        String applicationNumber);

        ApiResponseDTO<ClaimApplicationApprovalResponseDto> verifiedClaimActionClaimedBy(
                        String applicationNumber, String claimedBy);


        ApiResponseDTO<List<ClaimApplicationApprovalResponseDto>> getVerifiedClaimAndClaimedBy(String claimedBy);
                        
        ApiResponseDTO<ClaimApplicationApprovalResponseDto> verifiedClaimActionUnClaimedBy(
                        String applicationNumber, String unClaimedBy);

        ApiResponseDTO<ClaimApplicationApprovalResponseDto> verifiedClaimActionRejectedByApprover(
                        String applicationNumber, String rejectedBy, String rejectedRemarks);
}