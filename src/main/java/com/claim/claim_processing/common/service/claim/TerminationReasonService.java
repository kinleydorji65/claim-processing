package com.claim.claim_processing.common.service.claim;

import com.claim.claim_processing.common.DTO.request.claim.TerminationReasonCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.claim.TerminationReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.claim.TerminationReasonUpdateRequestDto;

import java.util.List;

public interface TerminationReasonService {

    ApiResponseDTO<List<TerminationReasonResponseDto>> getAllActive();

    ApiResponseDTO<TerminationReasonResponseDto> getById(Long id);

    ApiResponseDTO<TerminationReasonResponseDto> create(TerminationReasonCreateRequestDto requestDto);

    ApiResponseDTO<TerminationReasonResponseDto> update(Long id, TerminationReasonUpdateRequestDto requestDto);

    ApiResponseDTO<String> deactivate(Long id);
}