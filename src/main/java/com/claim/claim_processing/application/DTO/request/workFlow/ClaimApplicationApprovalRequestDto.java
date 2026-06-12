package com.claim.claim_processing.application.DTO.request.workFlow;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimApplicationApprovalRequestDto {

    private Long claimApplicationId;

    private Long approvalStatusId;
    private Long actionId;

    private BigDecimal approvedAmount;
    private BigDecimal approvedPfAmount;
    private BigDecimal approvedPensionAmount;
    private BigDecimal approvedWithdrawalAmount;
    private BigDecimal approvedRefundAmount;
    private BigDecimal approvedDeductionAmount;
    private BigDecimal finalNetPayableAmount;

    private ActivityEnum requiresManualReview;

    private String approverRemarks;

    private String approvedBy;
    private String approvedByRole;

    private ActivityEnum isActive;

    private String createdBy;
    private String updatedBy;
}