package com.claim.claim_processing.claim.DTO.application.response.workFlow;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationApprovalResponseDto {

    private Long id;

    private Long claimApplicationId;
    private Integer approvalLevel;

    // Approval Status
    private Long approvalStatusId;
    private String approvalStatusName;

    // Approval Decision
    private Long approvalDecisionId;
    private String approvalDecisionName;

    // Approved Amounts
    private BigDecimal approvedAmount;
    private BigDecimal approvedPfAmount;
    private BigDecimal approvedPensionAmount;
    private BigDecimal approvedWithdrawalAmount;
    private BigDecimal approvedRefundAmount;
    private BigDecimal approvedDeductionAmount;
    private BigDecimal finalNetPayableAmount;

    // Flags
    private ActivityEnum requiresFinanceAction;
    private ActivityEnum requiresManualReview;

    // Reason Codes
    private String approvalReasonCode;
    private String returnedReasonCode;
    private String rejectedReasonCode;

    // Approver Info
    private String approverRemarks;
    private String approvedBy;
    private String approvedByRole;
    private Timestamp approvedAt;

    // Audit
    private ActivityEnum isActive;
    private String createdBy;
    private Timestamp createdAt;
    private String updatedBy;
    private Timestamp updatedAt;
}
