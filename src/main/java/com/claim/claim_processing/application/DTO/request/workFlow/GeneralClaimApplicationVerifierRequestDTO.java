package com.claim.claim_processing.application.DTO.request.workFlow;

import java.util.List;

import com.claim.claim_processing.application.DTO.request.application.ClaimApplicationCalculationSummaryRequest;
import com.claim.claim_processing.application.DTO.request.detail.WrongRemitanceRequestDTO;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralClaimApplicationVerifierRequestDTO {
    private ClaimApplicationVerificationRequestDto verifierRequest;
    private ClaimApplicationCalculationSummaryRequest calculationSummary;
    
    private List<WrongRemitanceRequestDTO> wrongRemitanceRequestDTOs;
}
