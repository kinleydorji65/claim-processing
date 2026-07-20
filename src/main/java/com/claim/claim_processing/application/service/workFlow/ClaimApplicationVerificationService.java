package com.claim.claim_processing.application.service.workFlow;

import java.util.List;

import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationVerificationRequestDto;
import com.claim.claim_processing.application.DTO.request.workFlow.GeneralClaimApplicationVerifierRequestDTO;
import com.claim.claim_processing.application.DTO.response.application.GeneralClaimResponse;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationVerificationResponseDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;

public interface ClaimApplicationVerificationService {

        ApiResponseDTO<ClaimApplicationVerificationResponseDto> patch(
                        String applicationNumber,
                        ClaimApplicationVerificationRequestDto request);

        ApiResponseDTO<GeneralClaimResponse> verify(
                        String applicationNumber,
                        GeneralClaimApplicationVerifierRequestDTO request);

        ApiResponseDTO<ClaimApplicationVerificationResponseDto> getByVerifiedApplicationNumber(
                        String applicationNumber);
        ApiResponseDTO<ClaimApplicationVerificationResponseDto> getByApplicationNumber(String applicationNumber);

        ApiResponseDTO<List<ClaimApplicationVerificationResponseDto>> getVerifiedApplication();
        ApiResponseDTO<List<ClaimApplicationVerificationResponseDto>> getVerifiedClaim();

        ApiResponseDTO<List<ClaimApplicationVerificationResponseDto>> verifiedClaimApplicationClaimedBy(String applicationNumber, String claimedBy);
        ApiResponseDTO<List<ClaimApplicationVerificationResponseDto>> getClaimApplicationWhichClaimedBy(String claimedBy);
        ApiResponseDTO<List<ClaimApplicationVerificationResponseDto>> verifiedClaimApplicationUnClaimedBy(String applicationNumber, String unClaimedBy);
        
        ApiResponseDTO<ClaimApplicationVerificationResponseDto> rejectedClaimApplication(
                        String applicationNumber,
                        String rejectedBy, String remarks);
        
        ApiResponseDTO<List<ClaimApplicationVerificationResponseDto>> getVerifiedClaimButRejectedClaim();
}