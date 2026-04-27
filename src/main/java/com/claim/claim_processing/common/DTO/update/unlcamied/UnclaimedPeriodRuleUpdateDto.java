package com.claim.claim_processing.common.DTO.update.unlcamied;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnclaimedPeriodRuleUpdateDto {

    private String ruleName;
    private Integer periodValue;
    private String periodUnit;
    private ActivityEnum isActive; // "Y" / "N"
}