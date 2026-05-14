package com.claim.claim_processing.integration.loanAdjustment.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanDetailResponseDto {

    private String loanName;
    private String status;
    private Double loanAmount;
    private Double outstandingAmount;
}
