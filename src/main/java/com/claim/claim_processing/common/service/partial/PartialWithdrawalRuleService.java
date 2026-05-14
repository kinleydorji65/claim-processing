package com.claim.claim_processing.common.service.partial;

import com.claim.claim_processing.common.DTO.request.partial.PartialWithdrawalRuleRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalRuleResponseDto;

import java.util.List;

public interface PartialWithdrawalRuleService {

    ApiResponseDTO<PartialWithdrawalRuleResponseDto> create(PartialWithdrawalRuleRequestDto dto);

    ApiResponseDTO<PartialWithdrawalRuleResponseDto> update(Long id, PartialWithdrawalRuleRequestDto dto);

    ApiResponseDTO<PartialWithdrawalRuleResponseDto> getById(Long id);

    ApiResponseDTO<List<PartialWithdrawalRuleResponseDto>> getAll();

    ApiResponseDTO<List<PartialWithdrawalRuleResponseDto>> getByCategory(String categoryId);

    ApiResponseDTO<List<PartialWithdrawalRuleResponseDto>> getByReason(Long reasonId);

    ApiResponseDTO<List<PartialWithdrawalRuleResponseDto>> getByAccumulation(Long accumulationId);

    ApiResponseDTO<String> delete(
            Long id
    );
}