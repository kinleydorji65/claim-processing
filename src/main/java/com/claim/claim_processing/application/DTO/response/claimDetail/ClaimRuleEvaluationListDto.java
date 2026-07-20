package com.claim.claim_processing.application.DTO.response.claimDetail;

import java.time.LocalDateTime;
import java.util.List;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimRuleEvaluationListDto {
    private Long id;
    private Long calculationSummaryId;

    private String subClaimCode;
    private String subClaimType;
    private String subClaimDesc;
    private String ruleCode;

    private ActivityEnum isRuleApplied;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime evaluatedAt;

    private String remarks;

    private ActivityEnum isActive;

    private List<ClaimCalculationComponentDto> components;
}
