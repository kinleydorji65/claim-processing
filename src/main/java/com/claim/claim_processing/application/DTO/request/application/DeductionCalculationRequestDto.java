package com.claim.claim_processing.application.DTO.request.application;

import java.math.BigDecimal;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeductionCalculationRequestDto {

    private Long claimTypeId;

    private String nppfNumber;

    private BigDecimal grossPayableAmount;

    private String requestedBy;
}
