package com.claim.claim_processing.common.DTO.response.partial;

import com.claim.claim_processing.common.DTO.response.contribution.BenefitComponentTypeMasterResponseDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartialWithdrawalBenefitMapResponseDto {

    private Long id;
    private PartialWithdrawalAccumulationResponseDto accumulation;
    private BenefitComponentTypeMasterResponseDto benefitComponent;
}