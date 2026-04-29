package com.claim.claim_processing.common.service.common;

import com.claim.claim_processing.common.DTO.request.common.DecisionRequestDto;
import com.claim.claim_processing.common.DTO.response.common.DecisionResponseDto;

import java.util.List;

public interface DecisionService {

    DecisionResponseDto createDecision(DecisionRequestDto dto);

    DecisionResponseDto updateDecision(Long id, DecisionRequestDto dto);

    DecisionResponseDto getById(Long id);

    DecisionResponseDto getByCode(String code);

    List<DecisionResponseDto> getAll();

    List<DecisionResponseDto> getAllActive();

    void deleteDecision(Long id);
}