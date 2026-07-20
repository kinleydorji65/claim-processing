package com.claim.claim_processing.application.DTO.request.application;

import java.sql.Timestamp;
import java.util.List;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationRuleEvaluationRequestDto {
    private Long ruleEvaluationId;
    private String subRuleCode;
    @Builder.Default
    private ActivityEnum isRuleApplied = ActivityEnum.N;
    private String resultMessage;
    private Timestamp evaluatedAt;
    private String evaluatedBy;
    private String remarks;
    private String createdBy;
    private String updatedBy;

    private List<ClaimApplicationCalculationComponentRequestDto> components;
}
