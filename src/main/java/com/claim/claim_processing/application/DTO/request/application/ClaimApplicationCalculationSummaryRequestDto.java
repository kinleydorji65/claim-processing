package com.claim.claim_processing.application.DTO.request.application;

import lombok.*;

import java.math.BigDecimal;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationCalculationSummaryRequestDto {
    private BigDecimal finalPayableAmount;

    private BigDecimal numberOfServiceInYear;

    private BigDecimal actualAmountCalculated;

    @Builder.Default
    private String isPfEligible = "N";

    @Builder.Default
    private String isPensionEligible = "N";

    private BigDecimal totalContributionMonth;

    private String recommendedBenefitType;

    private String calculationRemarks;

    
    private Long calculationStatusId;

    @Builder.Default
    private ActivityEnum isActive = ActivityEnum.Y;

    private String createdBy;

    private String updatedBy;
}
