package com.claim.claim_processing.application.service.detail;

import com.claim.claim_processing.application.DTO.request.detail.NormalClaimRequestDto;
import com.claim.claim_processing.application.DTO.response.detail.NormalClaimResponseDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;

import java.util.List;

public interface NormalClaimService {

    ApiResponseDTO<NormalClaimResponseDto> create(NormalClaimRequestDto request);

    ApiResponseDTO<NormalClaimResponseDto> update(Long id, NormalClaimRequestDto request);

    ApiResponseDTO<NormalClaimResponseDto> getById(Long id);

    ApiResponseDTO<NormalClaimResponseDto> getByClaimApplicationId(Long claimApplicationId);

    ApiResponseDTO<List<NormalClaimResponseDto>> getAll();
}