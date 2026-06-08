package com.claim.claim_processing.application.DTO.request.application;

import java.util.List;

import com.claim.claim_processing.application.DTO.request.detail.BeneficiarySettlementDetailRequestDto;
import com.claim.claim_processing.application.DTO.request.detail.NormalClaimRequestDto;
import com.claim.claim_processing.application.DTO.request.detail.PartialWithdrawalRequestDto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralClaimPatchRequest {
    private ClaimApplicationRequestDto claimApplication;
    private List<ClaimApplicationBankDetailRequestDto> claimApplicationBankDetail;
    private ClaimApplicationCalculationPatchRequestDto claimApplicationCalculation;
    private ClaimApplicationDeductionPatchRequestDto claimApplicationDeduction;
    private List<ClaimApplicationForfeitedComponentPatchRequestDto> claimApplicationForfeitedComponent;
    private ClaimApplicationRuleEvaluationRequestDto claimApplicationRuleEvaluation;
    private NormalClaimRequestDto normalClaim;
    private PartialWithdrawalRequestDto partialWithdrawal;
    private BeneficiarySettlementDetailRequestDto beneficiarySettlementDetail;

}
