package com.claim.claim_processing.common.DTO.request.claim;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimVestingRuleMasterRequestDto {

    private Long id;

    private String ruleCode;

    private String categoryId; // FK handling (IMPORTANT)

    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;

    private String refundType;

    private Integer minVestingMonths;

    private Integer maxVestingMonths;

    private String comparisonType;

    private String payoutResult;

    private String remarks;

    private ActivityEnum isActive;

    private String createdBy;

    private String updatedBy;
}