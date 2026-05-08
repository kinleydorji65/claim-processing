package com.claim.claim_processing.common.service.unclaimed;

import com.claim.claim_processing.common.DTO.request.unclaimed.UnclaimedInterestFreezeRuleRequestDto;
import com.claim.claim_processing.common.DTO.response.unclaimed.UnclaimedInterestFreezeRuleResponseDto;

import java.util.List;

public interface InterestFreezeRuleService {

    UnclaimedInterestFreezeRuleResponseDto create(UnclaimedInterestFreezeRuleRequestDto dto);

    UnclaimedInterestFreezeRuleResponseDto update(Long id, UnclaimedInterestFreezeRuleRequestDto dto);

    UnclaimedInterestFreezeRuleResponseDto getById(Long id);

    UnclaimedInterestFreezeRuleResponseDto getByCode(String code);

    List<UnclaimedInterestFreezeRuleResponseDto> getAll();

    List<UnclaimedInterestFreezeRuleResponseDto> getAllActive();

    void delete(Long id);
}