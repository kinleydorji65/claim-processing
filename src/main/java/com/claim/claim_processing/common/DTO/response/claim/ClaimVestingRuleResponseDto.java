package com.claim.claim_processing.common.DTO.response.claim;

import com.claim.claim_processing.common.DTO.response.common.RuleTypeResponseDto;
import com.claim.claim_processing.common.DTO.response.others.agency.agencyRelated.AgencyCategoryDTO;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimVestingRuleResponseDto {

    private Long id;
    private String ruleCode;
    private AgencyCategoryDTO category;

    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    private String refundType;

    private Integer minVestingMonths;
    private Integer maxVestingMonths;

    private String comparisonType;

    private String payoutResult;

    private ClaimVestingCutoffResponseDto cutoff;

    private VestingRefundTypeResponseDto refund;
    private RuleTypeResponseDto ruleType;


    private String remarks;

    private ActivityEnum isActive;

    private String createdBy;
    private LocalDateTime createdAt;

    private String updatedBy;
    private LocalDateTime updatedAt;
}