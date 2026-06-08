package com.claim.claim_processing.application.DTO.response.calculation;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationRuleEvaluationListDto {

    private Long id;

    private Long claimApplicationId;
    private Long calculationSummaryId;

    private String subClaimCode;
    private String subClaimType;
    private String subClaimDesc;
    private String ruleCode;

    private ActivityEnum isRuleApplied;

    private String resultMessage;

    private String evaluatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime evaluatedAt;

    private String remarks;

    private ActivityEnum isActive;

    private List<ClaimApplicationCalculationComponentDto> components;
}