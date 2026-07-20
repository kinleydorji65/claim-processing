package com.claim.claim_processing.application.DTO.request.workFlow;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationVerificationRequestDto {
    private Long claimApplicationId;
    private String remarks;

    private String verifiedBy;
}