package com.claim.claim_processing.common.service.unclaimed;

import com.claim.claim_processing.common.DTO.request.unclaimed.UnclaimedPeriodRuleRequestDto;
import com.claim.claim_processing.common.DTO.response.unclaimed.UnclaimedPeriodRuleResponseDto;

import java.util.List;
import java.util.Optional;

public interface UnclaimedPeriodRuleMasterService {

    UnclaimedPeriodRuleResponseDto create(UnclaimedPeriodRuleRequestDto dto);

    UnclaimedPeriodRuleResponseDto update(Long id, UnclaimedPeriodRuleRequestDto dto);

    UnclaimedPeriodRuleResponseDto getById(Long id);

    UnclaimedPeriodRuleResponseDto getByRuleName(String ruleName);

    List<UnclaimedPeriodRuleResponseDto> getAll();

    List<UnclaimedPeriodRuleResponseDto> getAllActive();

    UnclaimedPeriodRuleResponseDto getByPeriodValue(Integer periodValue);

    void delete(Long id);
}