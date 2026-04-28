package com.claim.claim_processing.common.DTO.request.claim;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimLapsedRefundRequestDto {

    private String ruleCode;
    private String ruleName;
    private String claimCategoryCode;

    private Long claimCircumstanceId;
    private Long cessationTypeId;
    private Long schemeTypeId;

    private Integer minContributionMonths;
    private Integer maxContributionMonths;

    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    private String remarks;

    @Builder.Default
    private ActivityEnum isActive = ActivityEnum.Y;
}