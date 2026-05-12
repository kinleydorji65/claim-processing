package com.claim.claim_processing.common.service.common;

import com.claim.claim_processing.common.DTO.request.common.DecisionRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.common.DecisionResponseDto;

import java.util.List;

public interface DecisionService {

    ApiResponseDTO<DecisionResponseDto> createDecision(DecisionRequestDto requestDto);

    ApiResponseDTO<List<DecisionResponseDto>> getAll();

    ApiResponseDTO<List<DecisionResponseDto>> getAllActive();

    ApiResponseDTO<DecisionResponseDto> getById(Long id);

    ApiResponseDTO<DecisionResponseDto> getByCode(String code);

    ApiResponseDTO<DecisionResponseDto> updateDecision(Long id, DecisionRequestDto requestDto);

    ApiResponseDTO<String> deleteDecision(Long id);
}