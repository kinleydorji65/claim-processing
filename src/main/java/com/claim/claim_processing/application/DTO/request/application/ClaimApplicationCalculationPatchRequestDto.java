package com.claim.claim_processing.application.DTO.request.application;

import java.math.BigDecimal;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationCalculationPatchRequestDto {
    private Long calculationSummaryId;
    
    private BigDecimal finalPayableAmount;

    private BigDecimal actualAmountCalculated;
    private BigDecimal totalAmount;

    private Long calculationStatusId;

    private Long calculationStageId;

    private String recommendedBenefitType;

    private String remarks;

    private String updatedBy;
}
