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
public class GeneralClaimCreateRequest {
    private ClaimApplicationRequestDto claimApplication;
    private ClaimApplicationOtherRequestDto claimApplicationOther;
    private List<ClaimApplicationBankDetailRequestDto> bankDetails;
    private NormalClaimRequestDto normalClaim;
    private PartialWithdrawalRequestDto partialWithdrawal;
    private BeneficiarySettlementDetailRequestDto beneficiarySettlement;
}
