package com.claim.claim_processing.application.DTO.request.workFlow;

import com.claim.claim_processing.application.DTO.request.application.ClaimApplicationCalculationSummaryRequest;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralClaimApplicationVerifierRequestDTO {
    private ClaimApplicationVerificationRequestDto verifierRequest;
    private ClaimApplicationCalculationSummaryRequest calculationSummary;
}
