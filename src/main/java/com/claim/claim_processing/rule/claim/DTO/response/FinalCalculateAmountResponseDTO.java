package com.claim.claim_processing.rule.claim.DTO.response;

import java.math.BigDecimal;

import lombok.*;

@Data
@Builder
public class FinalCalculateAmountResponseDTO {
    private BigDecimal finalAmount;
    private BigDecimal balanceLoan;
    private BigDecimal balanceRental;
    private String loanNote;
    private String rentalNote;
    private String partialNote;
}
