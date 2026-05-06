package com.claim.claim_processing.common.DTO.response.claim;

import com.claim.claim_processing.common.DTO.response.contribution.BenefitComponentTypeMasterResponseDto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VestingRefundBenefitMapResponseDto {

    private Long id;

    private BenefitComponentTypeMasterResponseDto benefitComponentType;

    private VestingRefundTypeResponseDto vestingRefundType;
}
