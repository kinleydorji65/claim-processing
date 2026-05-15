package com.claim.claim_processing.common.DTO.update.claim;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimEligibilityUpdateRequestDto {

    private String ruleName;
    private String claimCategoryId;

    private Long claimCircumstanceId;
    private Long schemeTypeId;

    private String memberCategoryId;
    private List<Long> benefitTypeIds;
    private List<Long> existingBenefitTypeIds;

    private Integer minContributionMonths;
    private Integer maxContributionMonths;

    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    private String isActive;
    private String updatedBy;
}
