package com.claim.claim_processing.application.DTO.request.application;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralSpecialCaseApplicationRequest {
    private ClaimApplicationRequestDto claimApplication;
    private ClaimSpecialCaseApplicationRequestDto claimSpecialCaseApplicationRequestDto;
    private ClaimApplicationBankDetailRequestDto bankDetail;
}
