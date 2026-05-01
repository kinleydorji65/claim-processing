package com.claim.claim_processing.common.service.common;

import com.claim.claim_processing.common.DTO.request.common.StageRequestDto;
import com.claim.claim_processing.common.DTO.response.common.StageResponseDto;

import java.util.List;

public interface StageService {

    StageResponseDto create(StageRequestDto dto);

    StageResponseDto update(Long id, StageRequestDto dto);

    StageResponseDto getById(Long id);

    StageResponseDto getByCode(String code);

    List<StageResponseDto> getAll();

    List<StageResponseDto> getAllActive();

    void delete(Long id);
}