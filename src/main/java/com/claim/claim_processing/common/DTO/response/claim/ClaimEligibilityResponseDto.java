package com.claim.claim_processing.common.DTO.response.claim;
import com.claim.claim_processing.common.DTO.response.agency.AgencyCategoryResponseDto;
import com.claim.claim_processing.common.DTO.response.common.RuleTypeResponseDto;
import com.claim.claim_processing.common.DTO.response.contribution.BenefitComponentTypeMasterResponseDto;
import com.claim.claim_processing.common.DTO.response.contribution.SchemeTypeResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

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

    private List<AgencyCategoryResponseDto> agencyCategories;

    private List<BenefitComponentTypeMasterResponseDto> benefitComponents;
}