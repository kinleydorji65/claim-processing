package com.claim.claim_processing.claim.service.detail;

import com.claim.claim_processing.claim.DTO.request.detail.PartialWithdrawalRequestDto;
import com.claim.claim_processing.claim.DTO.response.detail.PartialWithdrawalResponseDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;

import java.util.List;

public interface PartialWithdrawalService {

    ApiResponseDTO<PartialWithdrawalResponseDto> create(PartialWithdrawalRequestDto request);

    ApiResponseDTO<PartialWithdrawalResponseDto> update(Long id, PartialWithdrawalRequestDto request);

    ApiResponseDTO<PartialWithdrawalResponseDto> getById(Long id);

    ApiResponseDTO<PartialWithdrawalResponseDto> getByClaimApplicationId(Long claimApplicationId);

    ApiResponseDTO<List<PartialWithdrawalResponseDto>> getAll();

    ApiResponseDTO<Void> delete(Long id);
}