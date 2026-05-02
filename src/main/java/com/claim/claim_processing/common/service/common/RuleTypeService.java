package com.claim.claim_processing.common.service.common;

import com.claim.claim_processing.common.DTO.request.common.RuleTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.common.RuleTypeResponseDto;

import java.util.List;

public interface RuleTypeService {

    RuleTypeResponseDto create(RuleTypeRequestDto dto);

    RuleTypeResponseDto update(Long id, RuleTypeRequestDto dto);

    RuleTypeResponseDto getById(Long id);

    RuleTypeResponseDto getByCode(String code);

    List<RuleTypeResponseDto> getAll();

    List<RuleTypeResponseDto> getAllActive();

    void delete(Long id);
}