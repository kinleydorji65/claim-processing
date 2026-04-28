package com.claim.claim_processing.claim.DTO.response.calculation;

import lombok.*;

import java.time.LocalDateTime;

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

    private String ruleTypeName;
    private String evaluationStageName;
    private String evaluationStatusName;

    private ActivityEnum isRuleMatched;
    private ActivityEnum isRuleApplied;

    private String evaluationResultCode;

    private String evaluatedBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime evaluatedAt;

    private ActivityEnum isActive;
}
