package com.claim.claim_processing.claim.DTO.workFlow;

import com.claim.claim_processing.common.DTO.response.common.DecisionResponseDto;
import com.claim.claim_processing.common.DTO.response.statusMaster.ApprovalStatusResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationApprovalResponseDto {

    private Long id;

    private Long claimApplicationId;
    private Integer approvalLevel;

    private ApprovalStatusResponseDto approvalStatus;
    private DecisionResponseDto approvalDecision;

    // ---------- Amounts ----------
    private BigDecimal approvedAmount;
    private BigDecimal approvedPfAmount;
    private BigDecimal approvedPensionAmount;
    private BigDecimal approvedWithdrawalAmount;
    private BigDecimal approvedRefundAmount;
    private BigDecimal approvedDeductionAmount;
    private BigDecimal finalNetPayableAmount;

    // ---------- Flags ----------
    private ActivityEnum requiresFinanceAction;
    private ActivityEnum requiresManualReview;
    private ActivityEnum isActive;

    // ---------- Reasoning ----------
    private String approvalReason;
    private String returnedReason;
    private String rejectedReason;

    private String approverRemarks;

    // ---------- Audit ----------
    private String approvedBy;
    private String approvedByRole;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approvedAt;

    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private String updatedBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
