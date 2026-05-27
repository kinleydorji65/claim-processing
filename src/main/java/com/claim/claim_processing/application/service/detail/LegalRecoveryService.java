package com.claim.claim_processing.application.service.detail;

import com.claim.claim_processing.application.DTO.request.detail.LegalRecoveryRequestDto;
import com.claim.claim_processing.application.DTO.response.detail.LegalRecoveryResponseDto;
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