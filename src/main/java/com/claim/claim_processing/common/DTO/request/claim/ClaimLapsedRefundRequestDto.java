package com.claim.claim_processing.common.DTO.request.claim;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimLapsedRefundRequestDto {

    private String ruleCode;
    private String ruleName;

    private Long claimCircumstanceId;
    private Long schemeTypeId;
    private Long ruleTypeId;

    private String memberCategoryId;
    private List<Long> benefitTypeIds;
    private List<Long> existingBenefitIds;

    private Integer minContributionMonths;
    private Integer maxContributionMonths;

    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    private String remarks;

    @Builder.Default
    private ActivityEnum isActive = ActivityEnum.Y;

    private String createdBy;
    private String updatedBy;
}