package com.claim.claim_processing.application.DTO.request.application;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationRuleEvaluationRequestDto {

    private Long calculationSummaryId;

    private Long ruleId;

    private String ruleCode;

    private String ruleName;

    private String isRuleMatched;

    private String isRuleApplied;

    private String resultMessage;

    private String evaluatedBy;

    private String remarks;

    private String isActive;

    private String createdBy;

    private String updatedBy;
}
