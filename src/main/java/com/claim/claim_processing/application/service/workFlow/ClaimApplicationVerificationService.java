package com.claim.claim_processing.application.service.workFlow;

import com.claim.claim_processing.application.DTO.request.workFlow.ClaimApplicationVerificationRequestDto;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationVerificationResponseDto;

import java.util.List;

public interface ClaimApplicationVerificationService {

    ClaimApplicationVerificationResponseDto create(
            ClaimApplicationVerificationRequestDto request
    );

    ClaimApplicationVerificationResponseDto update(
            Long id,
            ClaimApplicationVerificationRequestDto request
    );

    ClaimApplicationVerificationResponseDto getById(Long id);

    List<ClaimApplicationVerificationResponseDto> getByClaimApplicationId(
            Long claimApplicationId
    );

    List<ClaimApplicationVerificationResponseDto> getAll();
}