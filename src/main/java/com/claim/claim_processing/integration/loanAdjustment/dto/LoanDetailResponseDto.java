package com.claim.claim_processing.integration.loanAdjustment.dto;


import java.math.BigDecimal;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanDetailResponseDto {

    private Long loanId;
    private String loanName;
    private String status;
    private BigDecimal loanAmount;
    private BigDecimal outstandingAmount;
}
