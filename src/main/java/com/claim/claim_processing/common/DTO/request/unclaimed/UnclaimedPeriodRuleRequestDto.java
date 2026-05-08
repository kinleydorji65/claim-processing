package com.claim.claim_processing.common.DTO.request.unclaimed;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnclaimedPeriodRuleRequestDto {

    private String ruleName;
    private Integer periodValue;
    private String periodUnit;
    private ActivityEnum isActive;
}