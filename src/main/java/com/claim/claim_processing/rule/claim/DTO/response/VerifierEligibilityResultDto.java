package com.claim.claim_processing.rule.claim.DTO.response;

import java.util.List;

import lombok.*;

@Data
@Builder
public class VerifierEligibilityResultDto {
    private List<VerifierClaimCalculationResponseDTO.ComponentBalanceDTO> eligibleComponents;
}
