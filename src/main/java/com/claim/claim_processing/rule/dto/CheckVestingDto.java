package com.claim.claim_processing.rule.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckVestingDto {
    private String vestingNote;
    private String recommendedRefundType;
    private boolean vestingRuleFound;
    private Integer totalContributionMonths;
    private String loanNote;
}
