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
    @Builder.Default
    private String isRetained = "N";
    private BigDecimal outstandingAmount;
}
