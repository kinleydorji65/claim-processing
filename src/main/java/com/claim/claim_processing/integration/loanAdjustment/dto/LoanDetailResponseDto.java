package com.claim.claim_processing.integration.loanAdjustment.dto;


import java.math.BigDecimal;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanDetailResponseDto {

    private String loanName;
    private BigDecimal outstandingAmount;
}
