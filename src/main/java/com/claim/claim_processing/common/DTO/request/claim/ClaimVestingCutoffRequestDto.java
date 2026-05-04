package com.claim.claim_processing.common.DTO.request.claim;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimVestingCutoffRequestDto {

    private String ruleCode;
    private LocalDate cutoffDate;
    private Integer requiredMonths;
    private String description;
    private ActivityEnum isActive;
}