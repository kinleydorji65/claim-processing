package com.claim.claim_processing.common.service.claim;

import com.claim.claim_processing.common.DTO.response.claim.ClaimTypeRuleMapResponseDto;

import java.util.List;

public interface ClaimTypeRuleMapService {

    ClaimTypeRuleMapResponseDto create(List<Long> ruleIds, Long claimTypeId);

    ClaimTypeRuleMapResponseDto update(List<Long> ruleId, Long claimTypeId);

    List<ClaimTypeRuleMapResponseDto> getByClaimTypeId(Long claimTypeId);

    List<ClaimTypeRuleMapResponseDto> getByRuleTypeId(Long ruleTypeId);

    void delete(Long ruleId, Long claimTypeId);
}