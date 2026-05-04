package com.claim.claim_processing.common.DTO.request.claim;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimVestingRuleRequestDto {

    private String ruleCode;

    private String categoryId;

    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    private String refundType;

    private Integer minVestingMonths;
    private Integer maxVestingMonths;

    private String comparisonType;

    private String payoutResult;

    private Long cutoffId;

    private Long refundId;

    private Long ruleTypeId;

    private String remarks;
}