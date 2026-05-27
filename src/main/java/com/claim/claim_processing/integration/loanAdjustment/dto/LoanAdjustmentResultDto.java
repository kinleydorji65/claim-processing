package com.claim.claim_processing.integration.loanAdjustment.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanAdjustmentResultDto {
    private BigDecimal totalAdjustedAmount;
    private BigDecimal finalPayableAmount;

    private List<LoanDeductionDto> deductions;

    private String adjustmentNote;
}
