package com.claim.claim_processing.rule.claim.DTO.response;

import java.math.BigDecimal;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentalAdjustmentDetailDto {

    private Long rentalId;

    private String rentalName;

    private BigDecimal outstandingAmount;

    private BigDecimal adjustedAmount;

    private BigDecimal remainingOutstandingAmount;

    private String status;
}
