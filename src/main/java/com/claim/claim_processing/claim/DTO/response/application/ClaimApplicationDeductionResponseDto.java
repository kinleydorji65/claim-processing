package com.claim.claim_processing.claim.DTO.response.application;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationDeductionResponseDto {

    private Long id;

    // ---------------------------------
    // Parent Claim Application
    // ---------------------------------
    private Long claimApplicationId;

    // ---------------------------------
    // Deduction Type
    // ---------------------------------
    private Long deductionTypeId;
    private String deductionTypeCode;
    private String deductionTypeName;

    // ---------------------------------
    // Deduction Reference Type
    // ---------------------------------
    private Long deductionReferenceTypeId;
    private String deductionReferenceTypeCode;
    private String deductionReferenceTypeName;

    // ---------------------------------
    // Amount Details
    // ---------------------------------
    private BigDecimal outstandingAmount;
    private BigDecimal systemDeductedAmount;
    private BigDecimal verifiedDeductedAmount;
    private BigDecimal approvedDeductedAmount;
    private BigDecimal deductedAmount;

    // ---------------------------------
    // Priority
    // ---------------------------------
    private Integer priorityOrder;

    // ---------------------------------
    // Review Status
    // ---------------------------------
    private Long deductionReviewStatusId;
    private String deductionReviewStatusCode;
    private String deductionReviewStatusName;

    // ---------------------------------
    // Flags
    // ---------------------------------
    private ActivityEnum isAutoApplied;
    private ActivityEnum isManualOverride;
    private ActivityEnum isActive;

    // ---------------------------------
    // Override / Remarks
    // ---------------------------------
    private String overrideReason;
    private String remarks;

    // ---------------------------------
    // Child Details
    // ---------------------------------
    private List<ClaimApplicationLoanResponseDto> loanDetails;

    private List<ClaimApplicationRentalDeductionResponseDto> rentalDetails;

    private ClaimApplicationTdsTaxResponseDto tdsTaxDetail;

    // ---------------------------------
    // Audit
    // ---------------------------------
    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private String updatedBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
