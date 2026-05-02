package com.claim.claim_processing.common.DTO.response.claim;

import com.claim.claim_processing.common.DTO.response.common.RuleTypeResponseDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimTypeRuleMapResponseDto {

    private Long id;
    private ClaimTypeMasterResponseDto claimType;
    private RuleTypeResponseDto ruleType;
}