package com.claim.claim_processing.claim.DTO.calculation;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.claim.claim_processing.common.DTO.response.common.ReviewStatusResponseDto;
import com.claim.claim_processing.common.DTO.response.common.StageResponseDto;
import com.claim.claim_processing.common.DTO.response.refundMaster.RefundScopeResponseDto;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationRuleEvaluationResponseDto {
    // for detail view rules
    // Use this only when someone clicks “view details”.
    private Long id;

    private Long calculationSummaryId;
    private Long claimApplicationId;

    private RefundScopeResponseDto ruleType;
    private StageResponseDto evaluationStage;
    private ReviewStatusResponseDto evaluationStatus;

    private Long evaluationTriggerTypeId;

    // ---------- Flags ----------
    private String isRuleMatched;
    private String isRuleApplied;
    private String isManualOverride;

    // ---------- Override ----------
    private String overrideReason;

    private String evaluationResultCode;
    private String evaluationResultMessage;

    // ---------- Rule engine data ----------
    private String inputSnapshotJson;
    private String outputSnapshotJson;

    private String ruleFormula;
    private String ruleParametersJson;

    // ---------- Validity ----------
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate effectiveFrom;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate effectiveTo;

    // ---------- Audit ----------
    private String evaluatedBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime evaluatedAt;

    private String remarks;

    private String isActive;

    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private String updatedBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
