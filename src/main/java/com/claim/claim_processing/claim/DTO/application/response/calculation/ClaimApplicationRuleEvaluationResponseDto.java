package com.claim.claim_processing.claim.DTO.application.response.calculation;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationRuleEvaluationResponseDto {

    private Long id;

    private Long calculationSummaryId;
    private Long claimApplicationId;

    // Rule Type
    private Long ruleTypeId;
    private String ruleTypeName;

    // Evaluation Stage
    private Long evaluationStageId;
    private String evaluationStageName;

    private Long evaluationTriggerTypeId;

    // Rule Flags
    private ActivityEnum isRuleMatched;
    private ActivityEnum isRuleApplied;
    private ActivityEnum isManualOverride;

    private String overrideReason;

    // Evaluation Status
    private Long evaluationStatusId;
    private String evaluationStatusName;

    // Evaluation Result
    private String evaluationResultCode;
    private String evaluationResultMessage;

    // Snapshots
    private String inputSnapshotJson;
    private String outputSnapshotJson;

    // Rule Details
    private String ruleFormula;
    private String ruleParametersJson;

    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    // Evaluation Info
    private String evaluatedBy;
    private Timestamp evaluatedAt;

    private String remarks;

    // Audit
    private ActivityEnum isActive;
    private String createdBy;
    private Timestamp createdAt;
    private String updatedBy;
    private Timestamp updatedAt;
}
