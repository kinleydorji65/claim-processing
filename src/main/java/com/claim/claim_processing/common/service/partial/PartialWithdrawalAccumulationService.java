package com.claim.claim_processing.common.service.partial;

import com.claim.claim_processing.common.DTO.request.partial.PartialWithdrawalAccumulationRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalAccumulationResponseDto;

import java.util.List;

public interface PartialWithdrawalAccumulationService {

    ApiResponseDTO<PartialWithdrawalAccumulationResponseDto> create(PartialWithdrawalAccumulationRequestDto dto);

    ApiResponseDTO<PartialWithdrawalAccumulationResponseDto> update(Long id, PartialWithdrawalAccumulationRequestDto dto);

    ApiResponseDTO<PartialWithdrawalAccumulationResponseDto> getById(Long id);

    ApiResponseDTO<PartialWithdrawalAccumulationResponseDto> getByCode(
            String code
    );

    ApiResponseDTO<List<PartialWithdrawalAccumulationResponseDto>> getAll();

    ApiResponseDTO<List<PartialWithdrawalAccumulationResponseDto>> getAllActive();

    ApiResponseDTO<String> delete(
            Long id
    );
}