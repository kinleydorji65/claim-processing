package com.claim.claim_processing.claim.DTO.calculation;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.claim.claim_processing.common.DTO.response.arrRule.ArrRuleResponseDto;
import com.claim.claim_processing.common.DTO.response.arrRule.CreditMethodResponseDto;
import com.claim.claim_processing.common.DTO.response.calculationMaster.CalculationTriggerTypeResponseDto;
import com.claim.claim_processing.common.DTO.response.claim.CessationTypeResponseDto;
import com.claim.claim_processing.common.DTO.response.common.ReviewStatusResponseDto;
import com.claim.claim_processing.common.DTO.response.common.StageResponseDto;
import com.claim.claim_processing.common.DTO.response.statusMaster.CalculationStatusResponseDto;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationCalculationSummaryResponseDto {

    private Long id;

    private Long claimApplicationId;
    private Integer calculationRunNo;

    private StageResponseDto calculationStage;
    private CalculationTriggerTypeResponseDto calculationTriggerType;

    private CreditMethodResponseDto creditMethod;
    private ArrRuleResponseDto arrRule;

    // ---------- Flags ----------
    private String isPensionBalanceIncluded;

    // ---------- Dates ----------
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate calculationEffectiveDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime calculatedAt;

    // ---------- Amount breakdown ----------
    private BigDecimal baseAmount;
    private BigDecimal grossAmount;
    private BigDecimal deductionAmount;
    private BigDecimal loanAdjustmentAmount;
    private BigDecimal refundAmount;
    private BigDecimal recoveryAmount;

    private BigDecimal netPayableAmount;

    private BigDecimal systemCalculatedAmount;
    private BigDecimal verifiedAmount;
    private BigDecimal approvedAmount;
    private BigDecimal paidAmount;
    private BigDecimal postedAmount;

    // ---------- Difference / Audit ----------
    private String differenceReason;

    private ReviewStatusResponseDto finalPayableReviewStatus;
    private CalculationStatusResponseDto calculationStatus;

    private String calculationRemarks;

    private CessationTypeResponseDto currency;

    private String calculatedBy;

    // ---------- Children (only include if needed in detail view) ----------
    private List<ClaimApplicationCalculationComponentDto> calculationComponents;
    private List<ClaimApplicationRuleEvaluationListDto> ruleEvaluations;
    // ---------- Audit ----------
    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private String updatedBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}


// SUMMARY DTO
//    ↓
//    ├── COMPONENT LIST DTO (lightweight)
//    └── RULE EVALUATION LIST DTO (lightweight)

// DETAIL DTO
//    ├── COMPONENT DETAIL DTO
//    └── RULE EVALUATION DETAIL DTO