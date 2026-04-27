package com.claim.claim_processing.common.DTO.request.unclaimed;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnclaimedPeriodRuleRequestDto {

    private String ruleName;
    private Integer periodValue;
    private String periodUnit; // e.g. "DAYS", "MONTHS", "YEARS"
}