package com.claim.claim_processing.rule.claim.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanAdjustmentDetailDto {

    private Long loanTypeId;

    private String loanTypeName;

    private Integer priorityOrder;

    private BigDecimal outstandingAmount;

    private BigDecimal adjustedAmount;

    private BigDecimal remainingOutstandingAmount;

    private String status;
}
