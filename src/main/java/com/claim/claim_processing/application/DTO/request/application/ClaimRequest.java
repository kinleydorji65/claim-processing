package com.claim.claim_processing.application.DTO.request.application;

import com.claim.claim_processing.application.DTO.request.detail.NormalClaimRequestDto;
import com.claim.claim_processing.application.DTO.request.detail.PartialWithdrawalRequestDto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimRequest {
    private ClaimApplicationRequestDto claimApplication;
    private ClaimApplicationOtherRequestDto claimApplicationOther;
    private ClaimApplicationBankDetailRequestDto bankDetail;
    private NormalClaimRequestDto normalClaim;
    private PartialWithdrawalRequestDto partialWithdrawal;
}
