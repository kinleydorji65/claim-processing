package com.claim.claim_processing.application.DTO.request.application;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationDeductionDetailRequestDto {

    private Long claimApplicationId;

    private Long deductionTypeId;

    private BigDecimal outstandingAmount;

    private BigDecimal systemDeductedAmount;

    private BigDecimal verifiedDeductedAmount;

    private BigDecimal approvedDeductedAmount;

    private BigDecimal deductedAmount;

    private Integer priorityOrder;

    private Long deductionReviewStatusId;

    private String isAutoApplied;

    private String isManualOverride;

    private String overrideReason;

    private String remarks;

    private String createdBy;

    private String updatedBy;

    private String isActive;
}
