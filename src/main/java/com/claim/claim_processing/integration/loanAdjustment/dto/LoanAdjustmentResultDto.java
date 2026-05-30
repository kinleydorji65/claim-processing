package com.claim.claim_processing.integration.loanAdjustment.dto;

import java.math.BigDecimal;
import java.util.List;

import com.claim.claim_processing.rule.claim.DTO.response.LoanAdjustmentDetailDto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanAdjustmentResultDto {
    private BigDecimal totalAdjustedAmount;
    private BigDecimal finalPayableAmount;

    private List<LoanAdjustmentDetailDto> deductions;

    private String adjustmentNote;
}
