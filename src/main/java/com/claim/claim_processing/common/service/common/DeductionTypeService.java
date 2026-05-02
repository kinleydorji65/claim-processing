package com.claim.claim_processing.common.service.common;

import com.claim.claim_processing.common.DTO.request.common.DeductionTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.common.DeductionTypeResponseDto;

import java.util.List;

public interface DeductionTypeService {

    DeductionTypeResponseDto create(DeductionTypeRequestDto dto);

    DeductionTypeResponseDto getById(Long id);

    List<DeductionTypeResponseDto> getAllActive();

    DeductionTypeResponseDto update(Long id, DeductionTypeRequestDto dto);

    DeductionTypeResponseDto getByCode(String code);

    void delete(Long id);
}