package com.claim.claim_processing.common.DTO.response.claim;
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
    private String claimCategoryCode;

    private Long claimCircumstanceId;
    private String claimCircumstanceName;

    private Long cessationTypeId;
    private String cessationTypeName;

    private Long schemeTypeId;
    private String schemeTypeName;

    private Integer minContributionMonths;
    private Integer maxContributionMonths;

    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    private ActivityEnum isActive;
}