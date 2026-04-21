package com.claim.claim_processing.common.DTO.update.claim;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimEligibilityUpdateRequestDto {

    private String ruleName;
    private String claimCategoryCode;

    private Long claimCircumstanceId;
    private Long cessationTypeId;
    private Long schemeTypeId;

    private Integer minContributionMonths;
    private Integer maxContributionMonths;

    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    private String isActive;
}
