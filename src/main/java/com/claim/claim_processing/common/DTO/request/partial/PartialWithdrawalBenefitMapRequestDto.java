package com.claim.claim_processing.common.DTO.request.partial;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartialWithdrawalBenefitMapRequestDto {
    private Long id;
    private Long accumulationId;
    private Long benefitComponentId;
}