package com.claim.claim_processing.application.service.workFlow;

import java.util.List;

import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationVerificationRequestDto;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationVerificationResponseDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;

public interface ClaimApplicationVerificationService {

        ApiResponseDTO<ClaimApplicationVerificationResponseDto> patch(
                        String applicationNumber,
                        ClaimApplicationVerificationRequestDto request);

        ApiResponseDTO<ClaimApplicationVerificationResponseDto> verify(
                        String applicationNumber,
                        ClaimApplicationVerificationRequestDto request);

        ApiResponseDTO<ClaimApplicationVerificationResponseDto> getByApplicationNumber(
                        String applicationNumber);

        ApiResponseDTO<List<ClaimApplicationVerificationResponseDto>> verifiedClaimApplicationClaimedBy(String applicationNumber, String claimedBy);
}