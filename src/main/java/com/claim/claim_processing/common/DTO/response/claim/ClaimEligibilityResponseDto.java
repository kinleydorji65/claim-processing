package com.claim.claim_processing.common.DTO.response.claim;
import com.claim.claim_processing.common.DTO.response.common.RuleTypeResponseDto;
import com.claim.claim_processing.common.DTO.response.contribution.SchemeTypeResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimEligibilityResponseDto {

    private Long id;

    private String ruleCode;
    private String ruleName;

    private ClaimCircumstanceResponseDto claimCircumstance;
    private RuleTypeResponseDto ruleType;
    private SchemeTypeResponseDto schemeType;

    private Integer minContributionMonths;
    private Integer maxContributionMonths;

    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    private ActivityEnum isActive;
}