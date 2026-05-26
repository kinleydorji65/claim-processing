package com.claim.claim_processing.claim.service.detail;

import com.claim.claim_processing.claim.DTO.request.detail.LegalRecoveryRequestDto;
import com.claim.claim_processing.claim.DTO.response.detail.LegalRecoveryResponseDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;

import java.util.List;

public interface LegalRecoveryService {

    ApiResponseDTO<LegalRecoveryResponseDto> create(LegalRecoveryRequestDto request);

    ApiResponseDTO<LegalRecoveryResponseDto> update(Long id, LegalRecoveryRequestDto request);

    ApiResponseDTO<LegalRecoveryResponseDto> getById(Long id);

    ApiResponseDTO<LegalRecoveryResponseDto> getByClaimApplicationId(Long claimApplicationId);

    ApiResponseDTO<List<LegalRecoveryResponseDto>> getAll();

    ApiResponseDTO<Void> delete(Long id);
}