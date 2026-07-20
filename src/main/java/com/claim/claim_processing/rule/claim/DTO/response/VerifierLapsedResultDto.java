package com.claim.claim_processing.rule.claim.DTO.response;

import java.util.List;

import lombok.*;

@Data
@Builder
public class VerifierLapsedResultDto {
    private boolean forfeited;
    private List<VerifierClaimCalculationResponseDTO.ComponentBalanceDTO> forfeitedComponents;  // Changed this
    private List<String> forfeitedComponentCodes;
}
