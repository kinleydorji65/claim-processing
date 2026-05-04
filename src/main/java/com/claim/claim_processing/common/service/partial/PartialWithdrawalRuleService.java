package com.claim.claim_processing.common.service.partial;

import com.claim.claim_processing.common.DTO.request.partial.PartialWithdrawalRuleRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalRuleResponseDto;

import java.util.List;

public interface PartialWithdrawalRuleService {

    PartialWithdrawalRuleResponseDto create(PartialWithdrawalRuleRequestDto dto);

    PartialWithdrawalRuleResponseDto update(Long id, PartialWithdrawalRuleRequestDto dto);

    PartialWithdrawalRuleResponseDto getById(Long id);

    List<PartialWithdrawalRuleResponseDto> getAll();

    List<PartialWithdrawalRuleResponseDto> getByCategory(String categoryId);

    List<PartialWithdrawalRuleResponseDto> getByReason(Long reasonId);

    List<PartialWithdrawalRuleResponseDto> getByAccumulation(Long accumulationId);

    void delete(Long id);
}