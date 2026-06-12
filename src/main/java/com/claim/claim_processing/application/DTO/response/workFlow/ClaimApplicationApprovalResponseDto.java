package com.claim.claim_processing.application.DTO.response.workFlow;

import com.claim.claim_processing.common.DTO.response.others.StatusResponseDTO;
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
    private String applicationNumber;

    private StatusResponseDTO approvalStatus;

    // ---------- Amounts ----------
    private BigDecimal approvedAmount;
    private BigDecimal approvedPfAmount;
    private BigDecimal approvedPensionAmount;
    private BigDecimal approvedWithdrawalAmount;
    private BigDecimal approvedRefundAmount;
    private BigDecimal approvedDeductionAmount;
    private BigDecimal finalNetPayableAmount;

    // ---------- Flags ----------
    private ActivityEnum requiresManualReview;

    private String isActive;

    // ---------- Remarks ----------
    private String approverRemarks;

    // ---------- Approval Info ----------
    private String approvedBy;
    private String approvedByRole;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approvedAt;

    // ---------- Audit ----------
    private String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private String updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}