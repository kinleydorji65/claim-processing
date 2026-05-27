package com.claim.claim_processing.integration.loanAdjustment.dto;

import java.math.BigDecimal;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanDeductionDto {

    private String loanTypeName;

    private Long prioritySequence;

    private BigDecimal outstandingAmount;
}
