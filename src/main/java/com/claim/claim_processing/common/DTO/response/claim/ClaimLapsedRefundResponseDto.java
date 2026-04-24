package com.claim.claim_processing.common.DTO.response.claim;

import com.claim.claim_processing.common.DTO.response.contribution.SchemeTypeResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimLapsedRefundResponseDto {

    private Long id;

    // -------------------------------
    // RULE INFO
    // -------------------------------
    private String ruleCode;
    private String ruleName;

    private String claimCategoryCode;

    // -------------------------------
    // FK MASTERS
    // -------------------------------
    private ClaimCircumstanceResponseDto claimCircumstance;
    private CessationTypeResponseDto cessationType;
    private SchemeTypeResponseDto schemeType;

    // -------------------------------
    // ELIGIBILITY
    // -------------------------------
    private Integer minContributionMonths;
    private Integer maxContributionMonths;

    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    // -------------------------------
    // OTHER
    // -------------------------------
    private String remarks;

    // -------------------------------
    // STATUS
    // -------------------------------
    private ActivityEnum isActive;

    // -------------------------------
    // AUDIT
    // -------------------------------
    private String createdBy;
    private LocalDateTime createdAt;

    private String updatedBy;
    private LocalDateTime updatedAt;
}
