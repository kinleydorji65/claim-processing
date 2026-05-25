package com.claim.claim_processing.integration.rentalAdjustment.dto;

import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import lombok.*;

@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalDetailResponseDto {
    private String rentalType;
    private String status;
    private BigDecimal rentalPercentage;
    private BigDecimal amount;
    private BigDecimal rentalAmount;
}
