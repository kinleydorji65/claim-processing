package com.claim.claim_processing.common.service.partial;

import com.claim.claim_processing.common.DTO.request.partial.PartialWithdrawalCauseRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalCauseResponseDto;

import java.util.List;

public interface PartialWithdrawalCauseService {

    ApiResponseDTO<PartialWithdrawalCauseResponseDto> create(
            PartialWithdrawalCauseRequestDto requestDto
    );

    ApiResponseDTO<PartialWithdrawalCauseResponseDto> getById(
            Long id
    );

    ApiResponseDTO<PartialWithdrawalCauseResponseDto> getByCode(
            String code
    );

    ApiResponseDTO<List<PartialWithdrawalCauseResponseDto>> getAll();

    ApiResponseDTO<List<PartialWithdrawalCauseResponseDto>> getAllActive();

    ApiResponseDTO<List<PartialWithdrawalCauseResponseDto>> getByReason_Id(
            Long reasonId
    );

    ApiResponseDTO<PartialWithdrawalCauseResponseDto> update(
            Long id,
            PartialWithdrawalCauseRequestDto updateDto
    );

    ApiResponseDTO<String> delete(
            Long id
    );
}