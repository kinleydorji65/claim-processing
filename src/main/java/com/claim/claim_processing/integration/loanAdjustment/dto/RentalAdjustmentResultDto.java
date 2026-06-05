package com.claim.claim_processing.integration.loanAdjustment.dto;

import java.math.BigDecimal;
import java.util.List;

import com.claim.claim_processing.rule.claim.DTO.response.RentalAdjustmentDetailDto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentalAdjustmentResultDto {

    private BigDecimal totalAdjustedAmount;

    private BigDecimal finalPayableAmount;

    private List<RentalAdjustmentDetailDto> deductions;

    private String adjustmentNote;
}
