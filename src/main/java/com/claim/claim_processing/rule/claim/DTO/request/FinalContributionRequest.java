package com.claim.claim_processing.rule.claim.DTO.request;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinalContributionRequest {
    private String nppfNumber;
    private Long claimTypeId;
    private BigDecimal totalPfAmount;
    private BigDecimal totalPensionAmount;

    private BigDecimal totalPensionInterestAmount;
    private BigDecimal totalPfInterestAmount;

    private Long reasonId;
    private String categoryId;
    private Integer numberOfContributionMonths;
}
