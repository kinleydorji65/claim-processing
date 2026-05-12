package com.claim.claim_processing.common.DTO.request.claim;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimEligibilityCreateRequestDto {

    private String ruleCode;
    private String ruleName;

    private Long claimCircumstanceId;
    private Long schemeTypeId;
    private Long ruleTypeId;

    private String memberCategoryId;
    private Long benefitTypeId;

    private Integer minContributionMonths;
    private Integer maxContributionMonths;

    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    private String createdBy;
}