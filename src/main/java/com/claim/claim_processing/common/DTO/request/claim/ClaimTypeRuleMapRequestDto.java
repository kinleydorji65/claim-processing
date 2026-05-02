package com.claim.claim_processing.common.DTO.request.claim;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimTypeRuleMapRequestDto {

    private Long claimTypeId;
    private Long ruleId;
}