package com.claim.claim_processing.common.service.partial;

import com.claim.claim_processing.common.DTO.request.partial.PartialWithdrawalReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.partial.PartialWithdrawalReasonUpdateDto;

import java.util.List;

public interface PartialReasonService {

    ApiResponseDTO<PartialWithdrawalReasonResponseDto> create(PartialWithdrawalReasonRequestDto requestDto);

    ApiResponseDTO<PartialWithdrawalReasonResponseDto> getById(Long id);

    ApiResponseDTO<PartialWithdrawalReasonResponseDto> getByCode(String code);

    ApiResponseDTO<List<PartialWithdrawalReasonResponseDto>> getAll();

    ApiResponseDTO<List<PartialWithdrawalReasonResponseDto>> getAllActive();

    ApiResponseDTO<PartialWithdrawalReasonResponseDto> update(Long id, PartialWithdrawalReasonUpdateDto updateDto);

    ApiResponseDTO<String> delete(
            Long id
    );
}