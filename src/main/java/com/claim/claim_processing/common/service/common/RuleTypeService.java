package com.claim.claim_processing.common.service.common;

import com.claim.claim_processing.common.DTO.request.common.RuleTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.common.RuleTypeResponseDto;

import java.util.List;

public interface RuleTypeService {

    ApiResponseDTO<RuleTypeResponseDto> create(RuleTypeRequestDto dto);

    ApiResponseDTO<RuleTypeResponseDto> update(Long id, RuleTypeRequestDto dto);

    ApiResponseDTO<RuleTypeResponseDto> getById(Long id);

    ApiResponseDTO<RuleTypeResponseDto> getByCode(String code);

    ApiResponseDTO<List<RuleTypeResponseDto>> getAll();

    ApiResponseDTO<List<RuleTypeResponseDto>> getAllActive();

    ApiResponseDTO<String> delete(Long id);
}