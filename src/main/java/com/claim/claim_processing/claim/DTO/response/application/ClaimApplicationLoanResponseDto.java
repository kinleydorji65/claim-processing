package com.claim.claim_processing.claim.DTO.response.application;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationLoanResponseDto {

    private Long id;

    // ---------------------------------
    // Parent References
    // ---------------------------------
    private Long deductionDetailId;
    private Long claimApplicationId;

    // ---------------------------------
    // Loan Basic Info
    // ---------------------------------
    private String loanAccountNumber;

    private Long loanTypeId;
    private String loanTypeCode;
    private String loanTypeName;

    // ---------------------------------
    // Loan Dates
    // ---------------------------------
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate sanctionDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate emiStartDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate emiEndDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate loanStartDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate loanEndDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate loanSnapshotDate;

    // ---------------------------------
    // Guarantee
    // ---------------------------------
    private ActivityEnum isGuarantee;

    // ---------------------------------
    // Financial Details
    // ---------------------------------
    private BigDecimal outstandingPrincipal;
    private BigDecimal outstandingInterest;
    private BigDecimal penaltyAmount;
    private BigDecimal totalOutstanding;

    // ---------------------------------
    // Loan Status
    // ---------------------------------
    private Long loanStatusId;
    private String loanStatusCode;
    private String loanStatusName;

    // ---------------------------------
    // Review Status
    // ---------------------------------
    private Long loanReviewStatusId;
    private String loanReviewStatusCode;
    private String loanReviewStatusName;

    // ---------------------------------
    // Adjustment Decision
    // ---------------------------------
    private Long loanAdjustmentDecisionId;
    private String loanAdjustmentDecisionCode;
    private String loanAdjustmentDecisionName;

    // ---------------------------------
    // Verified Values
    // ---------------------------------
    private BigDecimal verifiedLoanDeductionAmount;
    private BigDecimal verifiedOutstandingBalance;

    // ---------------------------------
    // History / Remarks
    // ---------------------------------
    private String loanRecoveryHistoryText;
    private String remarks;

    // ---------------------------------
    // Audit
    // ---------------------------------
    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    private String updatedBy;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
