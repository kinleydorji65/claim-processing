package com.claim.claim_processing.common.service.partial;

import com.claim.claim_processing.common.DTO.request.partial.PartialCauseRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalCauseResponseDto;
import com.claim.claim_processing.common.DTO.update.partial.PartialCauseUpdateDto;

import java.util.List;

public interface PartialCauseService {

    ApiResponseDTO<PartialWithdrawalCauseResponseDto> create(PartialCauseRequestDto requestDto);

    ApiResponseDTO<PartialWithdrawalCauseResponseDto> getById(Long id);

    ApiResponseDTO<PartialWithdrawalCauseResponseDto> getByCode(String code);

    ApiResponseDTO<List<PartialWithdrawalCauseResponseDto>> getAll();

    ApiResponseDTO<List<PartialWithdrawalCauseResponseDto>> getAllActive();

    ApiResponseDTO<PartialWithdrawalCauseResponseDto> update(Long id, PartialCauseUpdateDto updateDto);

    ApiResponseDTO<String> delete(
            Long id
    );
}